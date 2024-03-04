package com.example.filmer.data

import android.os.Parcel
import android.os.Parcelable


data class FilmData(
    val posterId: Int,
    val title: String,
    val description: String,
    var isFavorite: Boolean = false,
    var rating: Int
) : Parcelable {

    val id = ids.id++

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt() == 1,
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(posterId)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeInt(if (isFavorite) 1 else 0)
        parcel.writeInt(rating)
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
    private object ids{
        var id = 0
    }

    override fun equals(other: Any?): Boolean {
        return (other is FilmData && other.id == this.id)
    }

    override fun toString(): String {
        return "id - $id"
    }
}