package com.example.reminder

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "events")
data class Event(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val date: String,  // Format: "yyyy-MM-dd"
    val time: String,  // Format: "HH:mm"
    val priority: Priority = Priority.MEDIUM,
    val isPinned: Boolean = false,
    val isRecurring: Boolean = false,
    val recurrencePattern: String? = null,
    val notificationEnabled: Boolean = true,
    val voiceNotificationEnabled: Boolean = false
) {
    enum class Priority { LOW, MEDIUM, HIGH }
}