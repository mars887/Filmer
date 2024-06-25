package com.example.filmer.viewmodel

import com.example.filmer.App
import com.example.filmer.alarms.AlarmController
import com.example.sql_module.AlarmInfo
import com.example.sql_module.sql.AlarmsDao
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class WatchLaterViewModel : DisposableViewModel() {

    @Inject
    lateinit var alarmsController: AlarmController

    val alarmsDatabase: Observable<List<AlarmInfo>>

    init {
        App.instance.appComponent.inject(this)
        alarmsDatabase = alarmsController.getAlarmList()
    }

    fun removeAlarm(alarmInfo: AlarmInfo) {
        alarmsController.deleteAlarm(alarmInfo)
    }

    fun updateAlarmDate(alarmInfo: AlarmInfo,alarmDate: String) {
        alarmsController.updateAlarmDate(alarmInfo, alarmDate)
    }
}