package com.example.filmer

import android.os.Parcel
import android.os.Parcelable


data class RData(
    val posterId: Int,
    val title: String,
    val description: String,
    var isFavorite: Boolean = false
) : Parcelable {

    val id = ids.id++

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt() == 1
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(posterId)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeInt(if (isFavorite) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RData> {
        override fun createFromParcel(parcel: Parcel): RData {
            return RData(parcel)
        }

        override fun newArray(size: Int): Array<RData?> {
            return arrayOfNulls(size)
        }
    }
    private object ids{
        var id = 0
    }

    override fun equals(other: Any?): Boolean {
        return (other is RData && other.id == this.id)
    }

    override fun toString(): String {
        return "id - $id"
    }
}