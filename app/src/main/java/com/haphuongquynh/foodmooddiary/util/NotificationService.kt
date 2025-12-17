package com.haphuongquynh.foodmooddiary.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.haphuongquynh.foodmooddiary.MainActivity
import com.haphuongquynh.foodmooddiary.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Notification Service - Day 23
 * Handles all notification-related functionality
 */
@Singleton
class NotificationService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val CHANNEL_ID_REMINDERS = "food_reminders"
        const val CHANNEL_ID_INSIGHTS = "food_insights"
        const val CHANNEL_ID_SYNC = "sync_updates"
        
        const val NOTIFICATION_ID_REMINDER = 1001
        const val NOTIFICATION_ID_INSIGHT = 1002
        const val NOTIFICATION_ID_SYNC = 1003
    }

    init {
        createNotificationChannels()
    }

    /**
     * Create notification channels for Android O+
     */
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = listOf(
                NotificationChannel(
                    CHANNEL_ID_REMINDERS,
                    "Food Reminders",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Daily reminders to log your meals"
                    enableVibration(true)
                },
                NotificationChannel(
                    CHANNEL_ID_INSIGHTS,
                    "Insights",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Weekly insights about your food and mood patterns"
                },
                NotificationChannel(
                    CHANNEL_ID_SYNC,
                    "Sync Updates",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Background data synchronization"
                }
            )

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            channels.forEach { notificationManager.createNotificationChannel(it) }
        }
    }

    /**
     * Show daily reminder notification
     */
    fun showReminderNotification(mealType: String) {
        if (!hasNotificationPermission()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 
            0, 
            intent, 
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_REMINDERS)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // TODO: Add proper icon
            .setContentTitle("Time for $mealType! 🍽️")
            .setContentText("Don't forget to log your meal and track your mood")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_REMINDER, notification)
    }

    /**
     * Show weekly insight notification
     */
    fun showInsightNotification(title: String, message: String) {
        if (!hasNotificationPermission()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "statistics")
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 
            0, 
            intent, 
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_INSIGHTS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_INSIGHT, notification)
    }

    /**
     * Show sync progress notification
     */
    fun showSyncNotification(isSyncing: Boolean, successCount: Int = 0, errorCount: Int = 0) {
        if (!hasNotificationPermission()) return

        val notification = if (isSyncing) {
            NotificationCompat.Builder(context, CHANNEL_ID_SYNC)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Syncing data...")
                .setContentText("Synchronizing your food entries")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .setProgress(0, 0, true)
                .build()
        } else {
            NotificationCompat.Builder(context, CHANNEL_ID_SYNC)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Sync completed")
                .setContentText("$successCount synced, $errorCount failed")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setAutoCancel(true)
                .build()
        }

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_SYNC, notification)
    }

    /**
     * Cancel all notifications
     */
    fun cancelAllNotifications() {
        NotificationManagerCompat.from(context).cancelAll()
    }

    /**
     * Check if notification permission is granted (Android 13+)
     */
    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}
