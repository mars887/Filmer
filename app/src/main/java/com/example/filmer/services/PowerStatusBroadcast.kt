package com.example.filmer.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class PowerStatusBroadcast(private val listener: (String?, Boolean?) -> Unit) :
    BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        listener(intent.action, intent.getBooleanExtra("state", false))
    }
}