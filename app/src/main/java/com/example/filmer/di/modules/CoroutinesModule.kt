package com.example.filmer.di.modules

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
class CoroutinesModule {
    @Singleton
    @Provides
    fun provideIOCoroutineScope(): CoroutineScope = CoroutineScope(Dispatchers.IO)
}