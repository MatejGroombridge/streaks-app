package dev.matejgroombridge.streaks.blocker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import dev.matejgroombridge.streaks.MainActivity
import dev.matejgroombridge.streaks.R

object BlockerNotifications {
    const val CHANNEL_ID = "blocker_status"
    const val NOTIFICATION_ID = 1001

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Blocker status",
            NotificationManager.IMPORTANCE_LOW,
        ).apply {
            description = "Shows when Streaks is actively filtering blocked sites."
            setShowBadge(false)
        }
        manager.createNotificationChannel(channel)
    }

    fun activeNotification(context: Context): Notification {
        ensureChannel(context)
        val openIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Streaks blocker is active")
            .setContentText("DNS protection is filtering blocked adult/custom domains.")
            .setContentIntent(openIntent)
            .setOngoing(true)
            .setShowWhen(false)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
}
