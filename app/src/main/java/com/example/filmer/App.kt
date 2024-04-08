package com.example.filmer

import android.app.Application
import android.content.Context
import com.example.filmer.di.AppComponent
import com.example.filmer.di.DaggerAppComponent
import com.example.filmer.di.modules.DomainModule
import com.example.filmer.di.modules.RemoteModule

class App : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        instance = this

        appComponent = DaggerAppComponent.builder()
            .remoteModule(RemoteModule())
            .domainModule(DomainModule(this))
            .build()
    }

    companion object {
        lateinit var instance: App
            private set
    }
}

val Context.appComponent: AppComponent
    get() = when (this) {
        is App -> appComponent
        else -> this.applicationContext.appComponent
    }