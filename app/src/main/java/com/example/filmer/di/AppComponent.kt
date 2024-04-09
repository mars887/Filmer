package com.example.filmer.di

import com.example.filmer.di.modules.DatabaseModule
import com.example.filmer.di.modules.DomainModule
import com.example.filmer.di.modules.RemoteModule
import com.example.filmer.viewmodel.SettingsFragmentViewModel
import com.example.filmer.viewmodel.TVFragmentViewModel
import com.example.filmer.views.fragments.TVFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        RemoteModule::class,
        DatabaseModule::class,
        DomainModule::class
    ]
)
interface AppComponent {
    fun inject(tvFragmentViewModel: TVFragmentViewModel)
    fun inject(tvFragmentViewModel: TVFragment)
    fun inject(settingsFragmentViewModel: SettingsFragmentViewModel)
}