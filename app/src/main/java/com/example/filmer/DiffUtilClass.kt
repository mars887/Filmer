package com.example.filmer

import androidx.recyclerview.widget.DiffUtil

class DiffUtilClass(val oldList: ArrayList<RData>, val newList: ArrayList<RData>) :
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
                old.posterId == new.posterId &&
                old.isFavorite == new.isFavorite
    }
}