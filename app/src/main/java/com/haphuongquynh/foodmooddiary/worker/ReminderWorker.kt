package com.haphuongquynh.foodmooddiary.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.haphuongquynh.foodmooddiary.util.notification.NotificationService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.*

/**
 * ReminderWorker - Day 23
 * Sends daily meal reminders at configured times
 */
@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val notificationService: NotificationService
) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "food_reminder_work"
        const val KEY_MEAL_TYPE = "meal_type"
    }

    override suspend fun doWork(): Result {
        return try {
            val mealType = inputData.getString(KEY_MEAL_TYPE) ?: getMealTypeFromTime()
            
            // Show reminder notification
            notificationService.showReminderNotification(mealType)
            
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    /**
     * Determine meal type based on current time
     */
    private fun getMealTypeFromTime(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 6..10 -> "Breakfast"
            in 11..14 -> "Lunch"
            in 17..21 -> "Dinner"
            else -> "Snack"
        }
    }
}
