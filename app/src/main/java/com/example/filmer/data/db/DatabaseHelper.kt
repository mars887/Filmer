package com.example.filmer.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import javax.inject.Inject

class DatabaseHelper @Inject constructor(context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "create table $TABLE_NAME (" +
                    "$COLUMN_ID integer primary key autoincrement," +
                    "$COLUMN_TITLE text unique," +
                    "$COLUMN_POSTER text," +
                    "$COLUMN_DESCRIPTION text," +
                    "$COLUMN_RATING real);"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    companion object {
        private const val DATABASE_NAME = "films.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_NAME = "films_table"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_POSTER = "poster_link"
        const val COLUMN_DESCRIPTION = "overview"
        const val COLUMN_RATING = "ratingAvg"
    }
}