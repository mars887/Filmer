package com.example.filmer.data.db

import android.content.ContentValues
import android.database.Cursor
import com.example.filmer.data.FilmData
import com.example.filmer.data.sql.FilmDBDao
import java.util.concurrent.Executors
import javax.inject.Inject

class SQLInteractor @Inject constructor(private val filmDBDao: FilmDBDao) {

    fun addFilmsToDb(films: List<FilmData>) {
        Executors.newSingleThreadExecutor().execute {
            filmDBDao.insertAll(films)
        }
    }

    fun setNewFilmsListToDb(films: List<FilmData>) {
        Executors.newSingleThreadExecutor().execute {
            filmDBDao.deleteAll()
            filmDBDao.insertAll(films)
        }
    }

    fun getAllFromDB(): List<FilmData> {
        return filmDBDao.getAllFilms()
    }
}