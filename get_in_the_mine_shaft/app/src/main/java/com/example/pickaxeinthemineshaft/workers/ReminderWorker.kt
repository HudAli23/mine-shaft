package com.example.pickaxeinthemineshaft.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.pickaxeinthemineshaft.R
import com.example.pickaxeinthemineshaft.data.TaskRepository
import com.example.pickaxeinthemineshaft.di.ChildWorkerFactory
import kotlinx.coroutines.flow.first
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class ReminderWorker @Inject constructor(
    context: Context,
    params: WorkerParameters,
    private val taskRepository: TaskRepository
) : CoroutineWorker(context, params) {

    companion object {
        const val CHANNEL_ID = "task_reminders"
        const val NOTIFICATION_ID = 1
    }

    override suspend fun doWork(): Result {
        val now = Date()
        val calendar = Calendar.getInstance()
        calendar.time = now
        calendar.add(Calendar.MINUTE, 15)
        val endTime = calendar.time

        // Check for tasks due soon
        val dueTasks = taskRepository.getTasksInDateRange(now, endTime).first()
        
        if (dueTasks.isNotEmpty()) {
            createNotificationChannel()

            val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setAutoCancel(true)

            val taskText = if (dueTasks.size == 1) {
                "Task '${dueTasks[0].title}' is due soon!"
            } else {
                "${dueTasks.size} tasks are due soon!"
            }

            notificationBuilder
                .setContentTitle("Task Reminder")
                .setContentText(taskText)
                .build()
                .let { notification ->
                    val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.notify(NOTIFICATION_ID, notification)
                }
        }

        return Result.success()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Task Reminders"
            val descriptionText = "Notifications for tasks"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    class Factory @Inject constructor(
        private val taskRepository: TaskRepository
    ) : ChildWorkerFactory {
        override fun create(appContext: Context, params: WorkerParameters): CoroutineWorker {
            return ReminderWorker(appContext, params, taskRepository)
        }
    }
}

interface ChildWorkerFactory {
    fun create(appContext: Context, params: WorkerParameters): CoroutineWorker
} 