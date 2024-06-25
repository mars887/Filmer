package com.example.filmer.alarms

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.filmer.appComponent
import com.example.sql_module.AlarmInfo
import com.example.sql_module.FilmData
import com.example.sql_module.sql.AlarmsDao
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject


class AlarmController @Inject constructor(val context: Context) {

    @Inject
    lateinit var alarmsDao: AlarmsDao

    @Inject
    lateinit var scope: CoroutineScope

    private val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private var injected = false

    fun restoreAlarms() {
        if (!injected) {
            context.appComponent.inject(this)
            injected = true
        }

        val disposable = alarmsDao.getAlarms().subscribe {
            it.filter {
                if (LocalDateTime.parse(it.alarmDate).isBefore(LocalDateTime.now().plusDays(1))) {
                    scope.launch {
                        alarmsDao.removeAlarm(it)
                    }
                    false
                } else true
            }.forEach {
                setupAlarm(it)
            }
        }
    }

    fun setupAlarm(selectedDateTime: LocalDateTime, film: FilmData) {
        if (!injected) {
            context.appComponent.inject(this)
            injected = true
        }

        val alarm = AlarmInfo(
            filmId = film.id,
            filmTitle = film.title,
            poster = film.poster,
            description = film.description,
            rating = film.rating,
            alarmDate = selectedDateTime.toString(),
            setupDate = LocalDateTime.now().toString()
        )

        scope.launch {
            try{
                alarmsDao.addAlarm(alarm)
            } catch (_: Exception) {
            }
        }

        setupAlarm(alarm)
    }
    fun setupAlarm(alarm: AlarmInfo) {
        scope.launch {
            createAlarm(alarm)
        }
    }

    private fun createAlarm(alarmInfo: AlarmInfo) {
        val pendingIntent = getPendingIntentForNBcast(alarmInfo)
        val alarmTime = LocalDateTime.parse(alarmInfo.alarmDate)

        alarmManager.set(
            AlarmManager.RTC,
            alarmTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            pendingIntent
        )

        println("alarm created ${alarmInfo.alarmDate}")
    }

    private fun getPendingIntentForNBcast(alarmInfo: AlarmInfo): PendingIntent {
        val intent = Intent(context, SendFilmNotificationBroadcast::class.java)
        putAlarmInfoToIntent(intent, alarmInfo)
        intent.action = "daxoFilmer.${alarmInfo.id}-${alarmInfo.filmTitle}"

        return PendingIntent.getBroadcast(
            context,
            alarmInfo.id,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun deleteAlarm(alarmInfo: AlarmInfo) {
        scope.launch {
            alarmsDao.removeAlarm(alarmInfo)
            alarmManager.cancel(getPendingIntentForNBcast(alarmInfo))
        }
    }

    fun getAlarmList(): Observable<List<AlarmInfo>> {
        return alarmsDao.getAlarms()
    }

    fun updateAlarmDate(alarmInfo: AlarmInfo, alarmDate: String) {
        scope.launch {
            alarmsDao.updateAlarmDate(alarmInfo.id, alarmDate)
            alarmInfo.alarmDate = alarmDate
            setupAlarm(alarmInfo)
        }
    }

    companion object {
        fun putAlarmInfoToIntent(intent: Intent, alarmInfo: AlarmInfo) {
            intent.putExtra("id", alarmInfo.id)
            intent.putExtra("alarmDate", alarmInfo.alarmDate)
            intent.putExtra("filmId", alarmInfo.filmId)
            intent.putExtra("filmTitle", alarmInfo.filmTitle)
            intent.putExtra("description", alarmInfo.description)
            intent.putExtra("rating", alarmInfo.rating)
            intent.putExtra("setupDate", alarmInfo.setupDate)
            intent.putExtra("poster", alarmInfo.poster)
        }
    }
}