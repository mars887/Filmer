package com.example.filmer.data.db

import android.content.ContentValues
import android.database.Cursor
import androidx.lifecycle.LiveData
import com.example.filmer.data.FilmData
import com.example.filmer.data.sql.FilmDBDao
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.Executors
import javax.inject.Inject

class SQLInteractor @Inject constructor(private val filmDBDao: FilmDBDao) {

    fun addFilmsToDb(films: List<FilmData>) {
            filmDBDao.insertAll(films)

    }

    fun setNewFilmsListToDb(films: List<FilmData>) {
            filmDBDao.deleteAll()
            filmDBDao.insertAll(films)

    }

    fun getAllFromDB(): Observable<List<FilmData>> = filmDBDao.getAllFilms()
}