package com.example.filmer.data

import androidx.lifecycle.LiveData
import com.example.filmer.data.sql.FilmDBDao
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.Executors
import javax.inject.Inject

class FilmDataBase @Inject constructor(private val filmDao: FilmDBDao) : DataBase<FilmData> {
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

    override fun getFilmDB(): Flow<List<FilmData>> = filmDao.getAllFilms()



    override fun addFilms(films: List<FilmData>) {
        Executors.newSingleThreadExecutor().execute {
            filmDao.insertAll(films)
        }

    }
}