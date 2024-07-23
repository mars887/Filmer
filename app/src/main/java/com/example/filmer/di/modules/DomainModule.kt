package com.example.filmer.di.modules

import android.app.AlarmManager
import android.content.Context
import androidx.core.content.ContextCompat.getSystemService
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import dagger.Module
import dagger.Provides

@Module
class DomainModule(val context: Context) {

    @Provides
    fun provideContext() = context

    @Provides
    fun provideFirebaseRemoteConfig(): FirebaseRemoteConfig {
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        val remConfigSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(0).build()
        remoteConfig.setConfigSettingsAsync(remConfigSettings)
        return remoteConfig
    }
}