package com.example.filmer.data

import javax.inject.Inject

class FilmDataBase @Inject constructor() : DataBase<FilmData> {
    val data = mutableListOf<FilmData>()
    private var lastLoadedPage = 1
    private var onCategory: String? = null

    fun getLastLoadedPage(category: String): Int {
        return if (category == onCategory) {
            lastLoadedPage
        } else {
            onCategory = category
            lastLoadedPage = 1
            1
        }
    }

    fun incrementLastLoadedPage() {
        lastLoadedPage++
    }

    fun resetPage() {
        lastLoadedPage = 1
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

    fun getSize(): Int = data.size


    fun clearBase() {
        resetPage()
        data.clear()
    }
}