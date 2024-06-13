package com.example.filmer


import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.appcompat.app.AppCompatDelegate
import com.example.filmer.data.PreferenceProvider
import com.example.filmer.di.AppComponent
import com.example.filmer.di.DaggerAppComponent
import com.example.filmer.di.modules.DomainModule
import com.example.filmer.domain.Interact
import com.example.filmer.notifications.NotificationHelper
import com.example.remote_module.DaggerRemoteComponent
import com.example.sql_module.DaggerSqlComponent
import javax.inject.Inject

class App : Application() {

    lateinit var appComponent: AppComponent
    @Inject
    lateinit var interactor: Interact
    @Inject
    lateinit var notificationsHelper: NotificationHelper

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

        appComponent.inject(this)

        val appTheme = interactor.getAppTheme()
        if(appTheme == PreferenceProvider.APP_THEME_LIGHT) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        notificationsHelper.init()
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

fun Context.isDarkThemeOn(): Boolean {
    return AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
}