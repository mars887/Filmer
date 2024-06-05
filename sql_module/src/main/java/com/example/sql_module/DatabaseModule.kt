package com.example.sql_module

import android.content.Context
import androidx.room.Room
import com.example.sql_module.sql.FavoritesDBDao
import com.example.sql_module.sql.FilmDBDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule() {

    @Singleton
    @Provides
    fun provideFilmDBDao(context: Context): FilmDBDao =
        Room.databaseBuilder(
            context,
            com.example.sql_module.sql.SQLFilmDatabase::class.java,
            "films_db"
        ).build().filmDao()

    @Singleton
    @Provides
    fun provideFavoritesDBDao(context: Context): FavoritesDBDao =
        Room.databaseBuilder(
            context,
            com.example.sql_module.sql.SQLFavoritesDatabase::class.java,
            "favorites_db"
        ).build().favoritesDao()
}