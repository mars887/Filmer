package com.example.sql_module.sql

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.sql_module.FilmData
import io.reactivex.rxjava3.core.Observable

@Dao
interface FilmDBDao {

    @Query("DELETE FROM cached_films")
    fun deleteAll()

    @Query("SELECT * FROM cached_films")
    fun getAllFilms(): Observable<List<FilmData>>

    @Query("SELECT * FROM cached_films WHERE title =:title")
    fun getFilmByTitle(title: String): Observable<FilmData> // TODO where - like title

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(films: List<FilmData>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFilm(film: FilmData)
}
