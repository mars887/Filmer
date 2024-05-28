package com.example.filmer.data.sql

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.filmer.data.FilmData

@Database(entities = [FilmData::class], version = 1, exportSchema = false)
abstract class SQLFilmDatabase : RoomDatabase() {
    abstract fun filmDao(): FilmDBDao
}

@Database(entities = [FavoriteFilmData::class], version = 1, exportSchema = false)
abstract class SQLFavoritesDatabase : RoomDatabase() {
    abstract fun favoritesDao(): FavoritesDBDao
}