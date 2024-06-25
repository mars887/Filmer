package com.example.filmer.alarms

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.filmer.appComponent
import com.example.filmer.notifications.NotificationHelper
import com.example.sql_module.AlarmInfo
import com.example.sql_module.FilmData
import com.example.sql_module.sql.AlarmsDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

class SendFilmNotificationBroadcast : BroadcastReceiver() {

    @Inject
    lateinit var notificationHelper: NotificationHelper

    @Inject
    lateinit var alarmsDao: AlarmsDao

    @Inject
    lateinit var scope: CoroutineScope

    override fun onReceive(context: Context, intent: Intent) {
        print("SendFilmNotificationBroadcast for")
        context.appComponent.inject(this)

        val film = FilmData(
            intent.getIntExtra("filmId", -1),
            intent.getStringExtra("poster") ?: "",
            intent.getStringExtra("filmTitle") ?: "unknown",
            intent.getStringExtra("description") ?: "unknown",
            intent.getDoubleExtra("rating", 0.0)
        )

        notificationHelper.sendWatchLater(film, intent.getIntExtra("id", -1))

        val alarmInfo = AlarmInfo(
            intent.getIntExtra("id", -1),
            intent.getIntExtra("filmId", -1),
            intent.getStringExtra("filmTitle") ?: "",
            intent.getStringExtra("alarmDate") ?: "",
            intent.getStringExtra("setupDate") ?: "",
            intent.getStringExtra("poster") ?: "",
            intent.getStringExtra("description") ?: "",
            intent.getDoubleExtra("rating", 0.0)
        )
        println("sending $alarmInfo")
    }
}