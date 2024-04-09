package com.example.filmer.data

import android.os.Parcel
import android.os.Parcelable


data class FilmData(
    val poster: String,
    val title: String,
    val description: String,
    var rating: Double,
    var isFavorite: Boolean = false
) : Parcelable {


    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readInt() == 1
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
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
        return (other is FilmData && other.title == this.title)
    }

    override fun toString(): String {
        return "id - $title"
    }
}