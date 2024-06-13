package com.example.filmer.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.filmer.R
import com.example.filmer.views.MainActivity
import com.example.sql_module.FilmData
import javax.inject.Inject

class NotificationHelper @Inject constructor(
    private val context: Context
) {
    private lateinit var notificationManager: NotificationManager

    fun init() {
        createNotificationChannel()
    }

    fun sendWatchLater(film: FilmData) {
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra("showDetailsID", film.id)
        intent.putExtra("showDetailsTitle", film.title)
        val pendingIntent =
            PendingIntent.getActivity(context, film.id, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = Notification.Builder(context, channelId)
            .setContentTitle("film notification!")
            .setContentText(film.title)
            .setContentIntent(pendingIntent)
            .setShowWhen(true)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        notificationManager.notify(film.id, notification)
    }

    private fun createNotificationChannel() {
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val descriptionText = "My Channel"

        val mChannel = NotificationChannel(channelId, channelName, channelImportance)
        mChannel.description = descriptionText

        notificationManager =
            context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
        //}
    }

    private companion object {
        const val channelName = "filmer notifications"
        const val channelId = "com.example.filmer.notifications_channel_id"
        const val channelImportance = NotificationManager.IMPORTANCE_DEFAULT
    }
}