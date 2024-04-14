package com.example.filmer.data.sql

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.filmer.data.FilmData

@Dao
interface FilmDBDao {

    @Query("DELETE FROM cached_films")
    fun deleteAll()

    @Query("SELECT * FROM cached_films")
    fun getAllFilms(): List<FilmData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(films: List<FilmData>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFilm(film: FilmData)
}
