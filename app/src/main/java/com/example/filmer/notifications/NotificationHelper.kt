package com.example.filmer.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.filmer.R
import com.example.filmer.alarms.AlarmController
import com.example.filmer.appComponent
import com.example.filmer.views.MainActivity
import com.example.sql_module.FilmData
import javax.inject.Inject

class NotificationHelper @Inject constructor(
    private val context: Context,
    private val alarmController: AlarmController
) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private var injected = false

    fun init() {
        createNotificationChannel()
    }

    fun sendWatchLater(film: FilmData, notificationId: Int) {

        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra("showDetailsID", film.id)
        intent.putExtra("showDetailsTitle", film.title)

        val pendingIntent =
            PendingIntent.getActivity(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        val initialNotification = Notification.Builder(context, channelId)
            .setContentTitle("It seems it's time to watch the movie")
            .setContentText(film.title)
            .setShowWhen(true)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        notificationManager.notify(notificationId, initialNotification)


        Glide.with(context)
            .asBitmap()
            .load(com.example.remote_module.entity.FilmApiConstants.IMAGES_URL + "w780" + film.poster)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val notification = Notification.Builder(context, channelId)
                        .setContentTitle("It seems it's time to watch the movie")
                        .setContentText(film.title)
                        .setShowWhen(true)
                        .setAutoCancel(true)
                        .setOnlyAlertOnce(true)
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setStyle(
                            Notification.BigPictureStyle()
                                .bigPicture(resource)
                        )
                        .build()

                    notificationManager.notify(notificationId, notification)
                }

                override fun onLoadCleared(placeholder: Drawable?) {}

            })

        println("notification for film ${film.title} created")

    }

    private fun createNotificationChannel() {
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val descriptionText = "filmerNotificationChannel"

        val mChannel = NotificationChannel(channelId, channelName, channelImportance)
        mChannel.description = descriptionText

        notificationManager.createNotificationChannel(mChannel)
        //}
    }

    private companion object {
        const val channelName = "filmer notifications"
        const val channelId = "com.example.filmer.notifications_channel_id"
        const val channelImportance = NotificationManager.IMPORTANCE_DEFAULT
    }
}