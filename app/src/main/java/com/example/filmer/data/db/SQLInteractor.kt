package com.example.filmer.data.db

import android.content.ContentValues
import android.database.Cursor
import com.example.filmer.data.FilmData
import javax.inject.Inject

class SQLInteractor @Inject constructor(databaseHelper: DatabaseHelper) {

    private val sqlDb = databaseHelper.readableDatabase

    private lateinit var cursor: Cursor

    fun addFilmsToDb(films: List<FilmData>) {
        films.forEach { film ->
            val cv = ContentValues()
            cv.apply {
                put(DatabaseHelper.COLUMN_TITLE, film.title)
                put(DatabaseHelper.COLUMN_POSTER, film.poster)
                put(DatabaseHelper.COLUMN_DESCRIPTION, film.description)
                put(DatabaseHelper.COLUMN_RATING, film.rating)
            }
            sqlDb.insert(DatabaseHelper.TABLE_NAME, null, cv)
        }
    }

    fun setNewFilmsListToDb(films: List<FilmData>) {
        sqlDb.delete(DatabaseHelper.TABLE_NAME, null, null)

        addFilmsToDb(films)
    }

    fun getDbSize(): Int {
        sqlDb.rawQuery("select count(*) from ${DatabaseHelper.TABLE_NAME}", null).use {
            it.moveToFirst()
            return it.getInt(0)
        }
    }

    fun getAllFromDB(): List<FilmData> {

        cursor = sqlDb.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_NAME}", null)

        val result = mutableListOf<FilmData>()

        if (cursor.moveToFirst()) {

            do {
                val title = cursor.getString(1)
                val poster = cursor.getString(2)
                val description = cursor.getString(3)
                val rating = cursor.getDouble(4)

                result.add(FilmData(poster,title, description, rating))
            } while (cursor.moveToNext())
        }
        return result
    }
}