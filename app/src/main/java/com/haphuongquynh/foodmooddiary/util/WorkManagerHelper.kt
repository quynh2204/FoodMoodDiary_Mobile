package com.haphuongquynh.foodmooddiary.util

import android.content.Context
import androidx.work.*
import com.haphuongquynh.foodmooddiary.worker.ReminderWorker
import com.haphuongquynh.foodmooddiary.worker.SyncWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * WorkManager Helper - Day 23
 * Schedules periodic tasks for reminders and sync
 */
@Singleton
class WorkManagerHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val workManager = WorkManager.getInstance(context)

    /**
     * Schedule daily meal reminders
     * Breakfast: 8:00, Lunch: 12:00, Dinner: 18:00
     */
    fun scheduleDailyReminders() {
        // Cancel existing reminders
        workManager.cancelUniqueWork(ReminderWorker.WORK_NAME)
        
        val reminders = listOf(
            Triple(8, 0, "Breakfast"),   // 8:00 AM
            Triple(12, 0, "Lunch"),      // 12:00 PM
            Triple(18, 0, "Dinner")      // 6:00 PM
        )
        
        reminders.forEach { (hour, minute, mealType) ->
            val workRequest = createReminderWorkRequest(hour, minute, mealType)
            workManager.enqueueUniquePeriodicWork(
                "${ReminderWorker.WORK_NAME}_$mealType",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }
    }

    /**
     * Create reminder work request with delay until next occurrence
     */
    private fun createReminderWorkRequest(
        hour: Int,
        minute: Int,
        mealType: String
    ): PeriodicWorkRequest {
        val currentTime = Calendar.getInstance()
        val targetTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            
            // If target time already passed today, schedule for tomorrow
            if (before(currentTime)) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        
        val initialDelay = targetTime.timeInMillis - currentTime.timeInMillis
        
        val inputData = workDataOf(ReminderWorker.KEY_MEAL_TYPE to mealType)
        
        return PeriodicWorkRequestBuilder<ReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
            .build()
    }

    /**
     * Schedule periodic data sync (every 6 hours)
     */
    fun schedulePeriodicSync() {
        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(6, TimeUnit.HOURS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                10,
                TimeUnit.MINUTES
            )
            .build()

        workManager.enqueueUniquePeriodicWork(
            SyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }

    /**
     * Trigger immediate sync
     */
    fun triggerImmediateSync() {
        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        workManager.enqueue(syncRequest)
    }

    /**
     * Cancel all scheduled work
     */
    fun cancelAllWork() {
        workManager.cancelUniqueWork(ReminderWorker.WORK_NAME)
        workManager.cancelUniqueWork(SyncWorker.WORK_NAME)
    }

    /**
     * Enable/Disable reminders
     */
    fun setRemindersEnabled(enabled: Boolean) {
        if (enabled) {
            scheduleDailyReminders()
        } else {
            workManager.cancelUniqueWork(ReminderWorker.WORK_NAME)
        }
    }
}
