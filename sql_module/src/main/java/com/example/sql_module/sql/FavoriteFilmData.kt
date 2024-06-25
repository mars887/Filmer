package com.example.sql_module.sql

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.sql_module.FilmData

@Entity(
    tableName = "favorite_films",
    indices = [Index(value = ["id"])]
)
class FavoriteFilmData(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "poster") val poster: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "rating")var rating: Double
) {
    fun toFilmData() = FilmData(
        id,poster, title, description, rating,true
    )
}