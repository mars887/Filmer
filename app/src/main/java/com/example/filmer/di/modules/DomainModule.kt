package com.example.filmer.di.modules

import android.app.AlarmManager
import android.content.Context
import androidx.core.content.ContextCompat.getSystemService
import dagger.Module
import dagger.Provides

@Module
class DomainModule(val context: Context) {
    
    @Provides
    fun provideContext() = context

}