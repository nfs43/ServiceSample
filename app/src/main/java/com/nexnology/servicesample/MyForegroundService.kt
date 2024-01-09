package com.nexnology.servicesample
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*

class MyForegroundService : Service() {

    private val notificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private val notificationId = 1
    private val channelId = "foreground_service_channel"
    private var job: Job? = null

    @SuppressLint("ForegroundServiceType")
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(notificationId, createNotification())

        // Start a background task (e.g., using a coroutine)
        job = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                // Your background task logic goes here
                delay(60000) // Simulating a task every minute
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Foreground Service")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentText("Running every minute")
            .setContentIntent(createPendingIntent())
            .setTicker("Foreground Service is running")
            .build()
    }

    private fun createPendingIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java)
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel() // Cancel the background task when the service is destroyed
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}