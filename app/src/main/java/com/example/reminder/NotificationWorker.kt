package com.example.reminder

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import java.util.Locale

class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private lateinit var tts: TextToSpeech

    override suspend fun doWork(): Result {
        val eventId = inputData.getString("event_id") ?: return Result.failure()
        val title = inputData.getString("title") ?: ""
        val description = inputData.getString("description") ?: ""
        val useVoice = inputData.getBoolean("voice_notification", false)

        // Show notification
        NotificationHelper.showNotification(
            context = applicationContext,
            title = title,
            message = description,
            eventId = eventId
        )

        // Voice notification
        if (useVoice) {
            tts = TextToSpeech(applicationContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    tts.language = Locale.getDefault()
                    tts.speak("Reminder: $title. $description", TextToSpeech.QUEUE_FLUSH, null, null)
                }
            }
        }

        return Result.success()
    }
}