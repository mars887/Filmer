package com.example.filmer.alarms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.filmer.appComponent
import javax.inject.Inject

class BootAlarmsRestore : BroadcastReceiver() {

    @Inject
    lateinit var alarmController: AlarmController

    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            context.appComponent.inject(this)

            alarmController.restoreAlarms()
        }
    }
}