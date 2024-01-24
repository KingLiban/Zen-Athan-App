package com.example.athanapp

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

@RequiresApi(Build.VERSION_CODES.O)
class AthanNotificationService(
    private val context: Context
) {
    companion object {
        const val CHANNEL_ID = "AthanNotificationService"
        const val CHANNEL_NAME = "AthanNotificationService"
        const val CHANNEL_DESCRIPTION = "AthanNotificationService"
    }

    fun showNotification(prayerName: String, prayerTime: String) {
        val activityIntent = Intent(context, MainActivity::class.java)
        val activityPendingIntent = PendingIntent.getActivity(
            context,
            0,
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )
        val contextText = if (prayerName == "Sunrise") {
            "The sun is rising!"
        } else {
            "It's time for prayer!"
        }
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("$prayerName at $prayerTime")
            .setContentText(contextText)
            .setSmallIcon(R.drawable.baseline_auto_awesome_24)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(activityPendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(prayerName.hashCode(), notification)
    }
}

class AthanNotificationReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "SHOW_NOTIFICATION") {
            val prayerName = intent.getStringExtra("prayerName")
            val prayerTime = intent.getStringExtra("prayerTime")
            if (context != null && prayerName != null && prayerTime != null) {
                AthanNotificationService(context).showNotification(prayerName, prayerTime)
            }
        }
    }
}