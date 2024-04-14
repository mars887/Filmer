package com.example.filmer.di.modules

import android.content.Context
import androidx.room.Room
import com.example.filmer.data.sql.SQLFilmDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideFilmDBDao(context: Context) =
        Room.databaseBuilder(
            context,
            SQLFilmDatabase::class.java,
            "films_db"
        ).build().filmDao()
}