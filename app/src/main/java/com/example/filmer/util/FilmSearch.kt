package com.example.filmer.util

import android.util.Log
import com.example.sql_module.FilmData
import com.example.filmer.views.rvadapters.RAdapter
import javax.inject.Inject

class FilmSearch {

    fun search(
        filmsDataBase: List<FilmData>,
        adapter: RAdapter,
        onlyFavorites: Boolean = false,
        query: String?
    ) {
        Log.d("bebe", "newList - ${filmsDataBase.size} : adapter - ${adapter.data.size}")

        val list = adapter.data
        var origin = mutableListOf<FilmData>()
        if (onlyFavorites) {
            filmsDataBase.forEach {
                if (it.isFavorite) origin.add(it)
            }
        } else {
            origin.addAll(filmsDataBase)
        }
        if (!query.isNullOrBlank()) {
            origin = origin.filter { it.title.lowercase().contains(query.lowercase()) }.toMutableList()
        }
        Log.d("bebe", "origin - ${origin.size}")

        var minus = 0
        filmsDataBase.forEachIndexed { i, data ->
            Log.d("bebe", "${origin.contains(data)} ${!list.contains(data)}")
            if (origin.contains(data)) {
                if (!list.contains(data)) {
                    list.add(i - minus, data)
                    adapter.notifyItemInserted(i - minus)
                }
            } else if (!origin.contains(data)) {
                if (list.contains(data)) {
                    list.removeAt(i - minus)
                    adapter.notifyItemRemoved(i - minus)
                }
                minus++
            }
        }
        val toRemove = list.filter { !origin.contains(it) }
        toRemove.forEach {
            val index = list.indexOf(it)
            list.remove(it)
            adapter.notifyItemRemoved(index)
        }
        Log.d("bebe", "post adapter - ${adapter.data.size}")
    }
}