package com.example.sql_module.sql

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.sql_module.AlarmInfo
import com.example.sql_module.FilmData

@Database(entities = [FilmData::class, FavoriteFilmData::class], version = 1, exportSchema = false)
abstract class SQLFilmDatabase : RoomDatabase() {
    abstract fun filmDao(): FilmDBDao
    abstract fun favoritesDao(): FavoritesDBDao
}


@Database(entities = [AlarmInfo::class],version = 1, exportSchema = false)
abstract class SQLAlarmsDao : RoomDatabase() {
    abstract fun alarmsDao(): AlarmsDao
}
