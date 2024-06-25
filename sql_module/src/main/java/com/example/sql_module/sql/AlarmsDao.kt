package com.example.sql_module.sql

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.sql_module.AlarmInfo
import io.reactivex.rxjava3.core.Observable

@Dao
interface AlarmsDao {

    @Insert
    fun addAlarm(alarmInfo: AlarmInfo)

    @Delete
    fun removeAlarm(alarmInfo: AlarmInfo)

    @Query("SELECT * FROM alarms_base ORDER BY alarmDate DESC")
    fun getAlarms(): Observable<List<AlarmInfo>>

    @Query("DELETE FROM alarms_base WHERE alarmDate < :date")
    fun clearAlarmsAfter(date: String)

    @Query("UPDATE alarms_base SET alarmDate = :alarmDate WHERE id = :alarmId")
    fun updateAlarmDate(alarmId: Int, alarmDate: String)
}