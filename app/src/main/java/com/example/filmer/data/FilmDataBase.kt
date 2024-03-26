package com.example.filmer.data

import javax.inject.Inject

class FilmDataBase @Inject constructor() : DataBase<FilmData> {
    val data = mutableListOf<FilmData>()
    private var lastLoadedPage = 1

    fun getLastLoadedPage() = lastLoadedPage
    fun incrementLastLoadedPage() {
        lastLoadedPage++
    }

    override fun getAll(): List<FilmData> {
        return data
    }

    override fun getByIndex(index: Int): FilmData {
        return data[index]
    }

    override fun addFilms(films: List<FilmData>) {
        films.forEach {
            if (data.all { film -> it.title != film.title }) {
                data.add(it)
            }
        }
    }
}