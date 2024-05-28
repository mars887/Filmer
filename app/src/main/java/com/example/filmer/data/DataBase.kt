package com.example.filmer.data

import io.reactivex.rxjava3.core.Observable

interface DataBase<T> {
    fun getFilmDB() : Observable<List<com.example.sql_module.FilmData>>
    fun addFilms(films: List<T>)
}