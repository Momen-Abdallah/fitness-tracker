package com.example.fitnesstracker.services

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.fitnesstracker.R
import okhttp3.internal.notify

class NotificationWorker(val context: Context,workerParameters: WorkerParameters) : Worker(context,workerParameters) {
    override fun doWork(): Result {
        val notification =  NotificationCompat.Builder(context,"")
            .setSmallIcon(R.drawable.notifications_24)
            .setContentTitle("Walking Reminder")
            .setContentText("Don't forget to walk today!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(1,notification)

        }
       return Result.success()
    }
}