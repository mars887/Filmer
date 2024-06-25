package com.example.sql_module

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms_base")
data class AlarmInfo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "filmId") val filmId: Int,
    @ColumnInfo(name = "filmTitle") val filmTitle: String,
    @ColumnInfo(name = "alarmDate") var alarmDate: String,
    @ColumnInfo(name = "setupDate") val setupDate: String,

    @ColumnInfo(name = "poster") val poster: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "rating") var rating: Double
) {
    fun toFilmData() = FilmData(
        filmId,poster,filmTitle,description,rating
    )

    override fun equals(other: Any?): Boolean {
        return other is AlarmInfo && other.id == id
    }

    override fun hashCode(): Int {
        return id
    }
}