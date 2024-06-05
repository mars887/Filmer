package com.example.sql_module

import com.example.sql_module.sql.FavoritesDBDao
import com.example.sql_module.sql.FilmDBDao

interface SqlProvider {
    fun provideFilmDBDao(): FilmDBDao
    fun provideFavoritesDBDao(): FavoritesDBDao
}