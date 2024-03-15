package com.example.filmer.data

interface DataBase<T> {
    fun getAll() : List<T>
    fun getByIndex(index: Int): T
    fun addFilms(films: List<T>)
}