package com.example.filmer.util

import androidx.recyclerview.widget.DiffUtil
import com.example.sql_module.FilmData

class DiffUtilClass(val oldList: ArrayList<com.example.sql_module.FilmData>, val newList: ArrayList<com.example.sql_module.FilmData>) :
    DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItemPosition == newItemPosition
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val old = oldList[oldItemPosition]
        val new = newList[newItemPosition]
        return old.description == new.description &&
                old.title == new.title &&
                old.poster == new.poster &&
                old.isFavorite == new.isFavorite
    }
}