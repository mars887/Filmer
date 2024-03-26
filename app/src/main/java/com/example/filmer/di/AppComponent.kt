package com.example.filmer.di

import com.example.filmer.di.modules.RemoteModule
import com.example.filmer.viewmodel.TVFragmentViewModel
import com.example.filmer.views.fragments.TVFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        RemoteModule::class
    ]
)
interface AppComponent {
    fun inject(tvFragmentViewModel: TVFragmentViewModel)
    fun inject(tvFragmentViewModel: TVFragment)
}