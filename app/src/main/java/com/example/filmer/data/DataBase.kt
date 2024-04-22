package com.example.filmer.data

import androidx.lifecycle.LiveData

interface DataBase<T> {
    fun getFilmDB() : LiveData<List<T>>
    fun addFilms(films: List<T>)
}