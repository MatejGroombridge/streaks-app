package dev.matejgroombridge.streaks.blocker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.VpnService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import dev.matejgroombridge.streaks.data.StreakRepository

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                val blocker = StreakRepository(context.applicationContext).state.first().blocker
                val permissionAlreadyGranted = VpnService.prepare(context) == null
                if (blocker.blockerEnabled && permissionAlreadyGranted) {
                    context.startForegroundService(Intent(context, BlockerVpnService::class.java))
                }
            }
            pending.finish()
        }
    }
}
