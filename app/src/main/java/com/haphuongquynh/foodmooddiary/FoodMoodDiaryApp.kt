package com.haphuongquynh.foodmooddiary

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.firebase.FirebaseApp
import com.haphuongquynh.foodmooddiary.util.WorkManagerHelper
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class FoodMoodDiaryApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    
    @Inject
    lateinit var workManagerHelper: WorkManagerHelper

    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        
        // Schedule periodic work
        schedulePeriodicWork()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()

    /**
     * Schedule periodic background work
     */
    private fun schedulePeriodicWork() {
        // Schedule daily reminders
        workManagerHelper.scheduleDailyReminders()
        
        // Schedule periodic sync every 6 hours
        workManagerHelper.schedulePeriodicSync()
    }
}
