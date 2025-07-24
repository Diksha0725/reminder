package com.example.reminder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.remember
import androidx.room.Room
import androidx.work.WorkManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize database and WorkManager
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "event-database"
        ).build()

        val workManager = WorkManager.getInstance(applicationContext)
        val viewModel = EventViewModel(db.eventDao(), workManager)

        // Create notification channel
        NotificationHelper.createNotificationChannel(this)

        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                AppNavigation(navController, viewModel)
            }
        }
    }
}