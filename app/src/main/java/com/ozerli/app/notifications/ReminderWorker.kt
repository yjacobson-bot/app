package com.ozerli.app.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.ozerli.app.data.AppDatabase
import java.util.concurrent.TimeUnit

class ReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val dao = AppDatabase.getInstance(context).requestDao()
        val now = System.currentTimeMillis()
        val allOpen = dao.getOverdueReminders(now)

        allOpen.forEach { request ->
            sendNotification(
                context,
                "תזכורת: ${request.personName}",
                if (request.shiur.isNotEmpty())
                    "${request.description} (${request.shiur})"
                else
                    request.description,
                request.id.toInt()
            )
        }

        return Result.success()
    }

    private fun sendNotification(
        context: Context,
        title: String,
        message: String,
        notificationId: Int
    ) {
        val channelId = "ozerli_reminders"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            "תזכורות עוזר לי",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "תזכורות על בקשות שממתינות לטיפול"
        }
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    companion object {
        const val WORK_NAME = "ozerli_reminder_work"

        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<ReminderWorker>(
                6, TimeUnit.HOURS
            ).setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(false)
                    .build()
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
        }
    }
}
