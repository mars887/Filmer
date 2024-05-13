package com.example.filmer.data

import androidx.lifecycle.LiveData
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.Flow

interface DataBase<T> {
    fun getFilmDB() : Observable<List<FilmData>>
    fun addFilms(films: List<T>)
}