package com.example.filmer


import android.app.Application
import android.content.Context
import com.example.filmer.di.AppComponent
import com.example.filmer.di.DaggerAppComponent
import com.example.filmer.di.modules.DomainModule
import com.example.remote_module.DaggerRemoteComponent
import com.example.sql_module.DaggerSqlComponent

class App : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        instance = this

        val remoteProvider = DaggerRemoteComponent.create()
        val sqlProvider = DaggerSqlComponent.builder().context(this).build()

        appComponent = DaggerAppComponent.builder()
            .sqlProvider(sqlProvider)
            .remoteProvider(remoteProvider)
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