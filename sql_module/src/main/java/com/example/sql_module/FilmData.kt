package com.example.sql_module

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.sql_module.sql.FavoriteFilmData

@Entity(tableName = "cached_films")
data class FilmData(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "poster") val poster: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "rating") var rating: Double,
    @ColumnInfo(name = "isFavorite") var isFavorite: Boolean = false
) : Parcelable {

    fun toFavorite() = FavoriteFilmData(
        id, poster, title, description, rating
    )


    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readInt() == 1
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(poster)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeDouble(rating)
        parcel.writeInt(if (isFavorite) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FilmData> {
        override fun createFromParcel(parcel: Parcel): FilmData {
            return FilmData(parcel)
        }

        override fun newArray(size: Int): Array<FilmData?> {
            return arrayOfNulls(size)
        }
    }

    override fun equals(other: Any?): Boolean {
        return (other is FilmData && other.id == this.id)
    }

    override fun toString(): String {
        return "id - $title"
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}