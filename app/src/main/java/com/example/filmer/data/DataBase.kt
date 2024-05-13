package com.example.filmer.data

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow

interface DataBase<T> {
    fun getFilmDB() : Flow<List<T>>
    fun addFilms(films: List<T>)
}