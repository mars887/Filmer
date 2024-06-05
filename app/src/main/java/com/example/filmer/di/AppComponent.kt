package com.example.filmer.di

import com.example.filmer.di.modules.CoroutinesModule
import com.example.sql_module.DatabaseModule
import com.example.filmer.di.modules.DomainModule
import com.example.filmer.viewmodel.SettingsFragmentViewModel
import com.example.filmer.viewmodel.TVFragmentViewModel
import com.example.filmer.views.fragments.FilmDetailsFragment
import com.example.filmer.views.fragments.TVFragment
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
}