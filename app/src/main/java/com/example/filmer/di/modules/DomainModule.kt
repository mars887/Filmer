package com.example.filmer.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class DomainModule(val context: Context) {
    
    @Provides
    fun provideContext() = context
}