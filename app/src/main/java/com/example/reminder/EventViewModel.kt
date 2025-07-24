package com.example.reminder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit

class EventViewModel(
    private val eventDao: EventDao,
    private val workManager: WorkManager
) : ViewModel() {

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events = _events.asStateFlow()

    init {
        viewModelScope.launch {
            eventDao.getAllEvents().collect { events ->
                _events.value = events
            }
        }
    }

    fun addEvent(event: Event) {
        viewModelScope.launch {
            eventDao.insertEvent(event)
            scheduleNotification(event)
        }
    }

    private fun scheduleNotification(event: Event) {
        if (!event.notificationEnabled) return

        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, event.date.substring(0, 4).toInt())
            set(Calendar.MONTH, event.date.substring(5, 7).toInt() - 1)
            set(Calendar.DAY_OF_MONTH, event.date.substring(8, 10).toInt())
            set(Calendar.HOUR_OF_DAY, event.time.substring(0, 2).toInt())
            set(Calendar.MINUTE, event.time.substring(3, 5).toInt())
            set(Calendar.SECOND, 0)
        }

        val delay = calendar.timeInMillis - System.currentTimeMillis()
        if (delay <= 0) return

        val data = Data.Builder()
            .putString("event_id", event.id)
            .putString("title", event.title)
            .putString("description", event.description)
            .putBoolean("voice_notification", event.voiceNotificationEnabled)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()

        workManager.enqueue(workRequest)
    }

    fun updateEvent(event: Event) {
        viewModelScope.launch {
            eventDao.updateEvent(event)
            // Reschedule notification if needed
            workManager.cancelAllWorkByTag(event.id)
            scheduleNotification(event)
        }
    }

    fun deleteEvent(eventId: String) {
        viewModelScope.launch {
            eventDao.deleteEvent(eventId)
            workManager.cancelAllWorkByTag(eventId)
        }
    }
}