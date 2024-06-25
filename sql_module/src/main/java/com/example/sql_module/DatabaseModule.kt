package com.example.sql_module

import android.content.Context
import androidx.room.Room
import com.example.sql_module.sql.AlarmsDao
import com.example.sql_module.sql.FavoritesDBDao
import com.example.sql_module.sql.FilmDBDao
import com.example.sql_module.sql.SQLAlarmsDao
import com.example.sql_module.sql.SQLFilmDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule() {

    @Singleton
    @Provides
    fun provideFilmDBDao(base: SQLFilmDatabase): FilmDBDao =
        base.filmDao()

    @Singleton
    @Provides
    fun provideFavoritesDBDao(base: SQLFilmDatabase): FavoritesDBDao =
        base.favoritesDao()

    @Singleton
    @Provides
    fun provideSQLDatabase(context: Context): SQLFilmDatabase =
        Room.databaseBuilder(
            context,
            SQLFilmDatabase::class.java,
            "films_db"
        ).build()

    @Singleton
    @Provides
    fun provideAlarmsDao(context: Context): AlarmsDao =
        Room.databaseBuilder(
            context,
            SQLAlarmsDao::class.java,
            "alarms_db"
        ).build().alarmsDao()
}