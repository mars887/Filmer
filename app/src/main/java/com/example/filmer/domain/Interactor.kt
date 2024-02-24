package com.example.filmer.domain

import com.example.filmer.data.FilmData
import com.example.filmer.data.FilmDataBase

class Interactor(private val dbase: FilmDataBase) {
    fun getFilmDataBase() : List<FilmData> = dbase.getAll()
}
