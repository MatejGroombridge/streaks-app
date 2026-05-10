package dev.matejgroombridge.streaks.blocker

import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import dev.matejgroombridge.streaks.data.BlockerState
import dev.matejgroombridge.streaks.data.StreakRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicReference

class BlockerVpnService : VpnService() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val blockerState = AtomicReference(BlockerState())
    private var vpnInterface: ParcelFileDescriptor? = null
    private var packetJob: Job? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> stopVpn()
            else -> startVpn()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        stopVpn()
        scope.cancel()
        super.onDestroy()
    }

    private fun startVpn() {
        startForeground(
            BlockerNotifications.NOTIFICATION_ID,
            BlockerNotifications.activeNotification(this),
        )
        if (vpnInterface != null) return
        scope.launch {
            StreakRepository(applicationContext).state.collectLatest { state ->
                blockerState.set(state.blocker)
            }
        }
        vpnInterface = Builder()
            .setSession("Streaks Blocker")
            .addAddress(VPN_ADDRESS, 32)
            .addDnsServer(VPN_DNS_SERVER)
            .addRoute(VPN_DNS_SERVER, 32)
            .setBlocking(false)
            .establish()

        val fd = vpnInterface?.fileDescriptor ?: return
        packetJob = scope.launch {
            val input = FileInputStream(fd)
            val output = FileOutputStream(fd)
            val buffer = ByteArray(MAX_PACKET_SIZE)
            while (true) {
                val length = input.read(buffer)
                if (length > 0) handlePacket(buffer.copyOf(length), output)
            }
        }
    }

    private fun stopVpn() {
        packetJob?.cancel()
        packetJob = null
        vpnInterface?.close()
        vpnInterface = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
        stopSelf()
    }

    private fun handlePacket(packet: ByteArray, output: FileOutputStream) {
        val parsed = DnsPacket.parse(packet) ?: return
        val state = blockerState.get()
        val blocked = DomainMatcher.isBlocked(parsed.queryName, state.blockAllPornSites, state.customSites)
        val response = if (blocked) {
            parsed.buildBlockedResponse()
        } else {
            parsed.forwardToUpstream(this) ?: return
        }
        output.write(response)
    }

    private data class DnsPacket(
        val packet: ByteArray,
        val ipHeaderLength: Int,
        val udpOffset: Int,
        val dnsOffset: Int,
        val dnsLength: Int,
        val queryName: String,
    ) {
        fun buildBlockedResponse(): ByteArray {
            val response = packet.copyOf()
            swapIpv4Addresses(response)
            swapUdpPorts(response)
            response[dnsOffset + 2] = (response[dnsOffset + 2].toInt() or 0x80).toByte() // QR=response
            response[dnsOffset + 3] = ((response[dnsOffset + 3].toInt() and 0xF0) or 0x03).toByte() // NXDOMAIN
            response[dnsOffset + 6] = 0
            response[dnsOffset + 7] = 0
            response[dnsOffset + 8] = 0
            response[dnsOffset + 9] = 0
            response[dnsOffset + 10] = 0
            response[dnsOffset + 11] = 0
            fixLengthsAndChecksums(response)
            return response
        }

        fun forwardToUpstream(service: VpnService): ByteArray? = runCatching {
            DatagramSocket().use { socket ->
                service.protect(socket)
                socket.soTimeout = 2500
                val query = packet.copyOfRange(dnsOffset, dnsOffset + dnsLength)
                socket.send(DatagramPacket(query, query.size, InetSocketAddress(UPSTREAM_DNS, 53)))
                val upstream = ByteArray(4096)
                val upstreamPacket = DatagramPacket(upstream, upstream.size)
                socket.receive(upstreamPacket)
                buildDnsResponse(upstream.copyOf(upstreamPacket.length))
            }
        }.getOrNull()

        private fun buildDnsResponse(dnsPayload: ByteArray): ByteArray {
            val response = packet.copyOf(ipHeaderLength + 8 + dnsPayload.size)
            swapIpv4Addresses(response)
            swapUdpPorts(response)
            System.arraycopy(dnsPayload, 0, response, dnsOffset, dnsPayload.size)
            fixLengthsAndChecksums(response)
            return response
        }

        private fun swapIpv4Addresses(bytes: ByteArray) {
            for (i in 0 until 4) {
                val tmp = bytes[12 + i]
                bytes[12 + i] = bytes[16 + i]
                bytes[16 + i] = tmp
            }
        }

        private fun swapUdpPorts(bytes: ByteArray) {
            for (i in 0 until 2) {
                val tmp = bytes[udpOffset + i]
                bytes[udpOffset + i] = bytes[udpOffset + 2 + i]
                bytes[udpOffset + 2 + i] = tmp
            }
        }

        private fun fixLengthsAndChecksums(bytes: ByteArray) {
            val totalLength = bytes.size
            bytes[2] = (totalLength ushr 8).toByte()
            bytes[3] = totalLength.toByte()
            val udpLength = totalLength - ipHeaderLength
            bytes[udpOffset + 4] = (udpLength ushr 8).toByte()
            bytes[udpOffset + 5] = udpLength.toByte()
            bytes[10] = 0
            bytes[11] = 0
            val ipChecksum = checksum(bytes, 0, ipHeaderLength)
            bytes[10] = (ipChecksum ushr 8).toByte()
            bytes[11] = ipChecksum.toByte()
            bytes[udpOffset + 6] = 0
            bytes[udpOffset + 7] = 0
        }

        companion object {
            fun parse(packet: ByteArray): DnsPacket? {
                if (packet.size < 28) return null
                val version = (packet[0].toInt() ushr 4) and 0x0F
                if (version != 4) return null
                val ihl = (packet[0].toInt() and 0x0F) * 4
                if (packet.size < ihl + 8 + 12) return null
                val protocol = packet[9].toInt() and 0xFF
                if (protocol != 17) return null // UDP only
                val udpOffset = ihl
                val destPort = u16(packet, udpOffset + 2)
                if (destPort != 53) return null
                val dnsOffset = udpOffset + 8
                val dnsLength = packet.size - dnsOffset
                val qdCount = u16(packet, dnsOffset + 4)
                if (qdCount < 1) return null
                val queryName = parseQueryName(packet, dnsOffset + 12, packet.size) ?: return null
                return DnsPacket(packet, ihl, udpOffset, dnsOffset, dnsLength, queryName)
            }

            private fun parseQueryName(bytes: ByteArray, start: Int, limit: Int): String? {
                val labels = mutableListOf<String>()
                var index = start
                while (index < limit) {
                    val length = bytes[index].toInt() and 0xFF
                    if (length == 0) break
                    if ((length and 0xC0) != 0) return null
                    index++
                    if (index + length > limit) return null
                    labels += bytes.copyOfRange(index, index + length).toString(Charsets.UTF_8)
                    index += length
                }
                return labels.joinToString(".").takeIf { it.isNotBlank() }
            }
        }
    }

    companion object {
        const val ACTION_STOP = "dev.matejgroombridge.streaks.blocker.STOP"
        private const val VPN_ADDRESS = "10.48.0.2"
        private const val VPN_DNS_SERVER = "10.48.0.1"
        private const val UPSTREAM_DNS = "1.1.1.1"
        private const val MAX_PACKET_SIZE = 32767

        fun stopIntent(context: android.content.Context): Intent = Intent(context, BlockerVpnService::class.java).apply {
            action = ACTION_STOP
        }

        private fun u16(bytes: ByteArray, offset: Int): Int =
            ((bytes[offset].toInt() and 0xFF) shl 8) or (bytes[offset + 1].toInt() and 0xFF)

        private fun checksum(bytes: ByteArray, offset: Int, length: Int): Int {
            var sum = 0L
            var i = offset
            while (i < offset + length - 1) {
                sum += (((bytes[i].toInt() and 0xFF) shl 8) or (bytes[i + 1].toInt() and 0xFF)).toLong()
                i += 2
            }
            if (length % 2 == 1) sum += ((bytes[offset + length - 1].toInt() and 0xFF) shl 8).toLong()
            while ((sum shr 16) != 0L) sum = (sum and 0xFFFF) + (sum shr 16)
            return sum.inv().toInt() and 0xFFFF
        }
    }
}
