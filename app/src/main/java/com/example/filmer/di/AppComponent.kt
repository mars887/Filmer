package com.example.filmer.di

import com.example.filmer.App
import com.example.filmer.alarms.AlarmController
import com.example.filmer.alarms.BootAlarmsRestore
import com.example.filmer.alarms.SendFilmNotificationBroadcast
import com.example.filmer.di.modules.CoroutinesModule
import com.example.filmer.di.modules.DomainModule
import com.example.filmer.notifications.NotificationHelper
import com.example.filmer.viewmodel.SettingsFragmentViewModel
import com.example.filmer.viewmodel.TVFragmentViewModel
import com.example.filmer.viewmodel.WatchLaterViewModel
import com.example.filmer.views.MainActivity
import com.example.filmer.views.fragments.FilmDetailsFragment
import com.example.filmer.views.fragments.ReklamaFragment
import com.example.filmer.views.fragments.TVFragment
import com.example.filmer.views.fragments.WatchLaterFragment
import com.example.filmer.views.rvadapters.AlarmsListRVAdapter
import com.example.filmer.views.rvadapters.RAdapter
import com.example.remote_module.RemoteProvider
import com.example.sql_module.SqlProvider
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    dependencies = [
        RemoteProvider::class,
        SqlProvider::class
    ],
    modules = [
        DomainModule::class,
        CoroutinesModule::class
    ]
)
interface AppComponent {
    fun inject(tvFragmentViewModel: TVFragmentViewModel)
    fun inject(tvFragmentViewModel: TVFragment)
    fun inject(settingsFragmentViewModel: SettingsFragmentViewModel)
    fun inject(RAdapter: RAdapter)
    fun inject(filmDetailsFragment: FilmDetailsFragment)
    fun inject(app: App)
    fun inject(bootAlarmsRestore: BootAlarmsRestore)
    fun inject(alarmController: AlarmController)
    fun inject(sendFilmNotificationBroadcast: SendFilmNotificationBroadcast)
    fun inject(notificationHelper: NotificationHelper)
    fun inject(alarmsListRVAdapter: AlarmsListRVAdapter)
    fun inject(watchLaterFragment: WatchLaterFragment)
    fun inject(watchLaterViewModel: WatchLaterViewModel)
    fun inject(reklamaFragment: ReklamaFragment)
    fun inject(mainActivity: MainActivity)
}