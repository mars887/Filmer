package com.example.filmer.data.sql

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.filmer.data.FilmData

@Entity(tableName = "favorite_films", indices = [Index(value = ["title"], unique = true)])
class FavoriteFilmData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "poster") val poster: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "rating") var rating: Double
) {

    constructor(filmData: FilmData) : this(filmData.id, filmData.poster,filmData.title, filmData.description, filmData.rating)

    fun toFilmData() = FilmData(
        id,
        poster,
        title,
        description,
        rating,
        true
    )

    override fun hashCode(): Int {
        return title.hashCode()
    }

    override fun toString(): String {
        return title
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FavoriteFilmData

        if (title != other.title) return false

        return true
    }
}