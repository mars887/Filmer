package com.example.filmer.data

import com.example.sql_module.FilmData
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.Flow

interface DataBase<T> {
    fun getFilmDB() : Observable<List<com.example.sql_module.FilmData>>
    fun addFilms(films: List<T>)
    fun getFilmById(it: Long): Flow<FilmData?>
}