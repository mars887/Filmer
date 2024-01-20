package com.example.filmer

import java.util.Locale

interface HasSearch {
    fun onQueryTextSubmit(text: String?)
    fun onQueryTextChange(text: String?)

    fun search(query: String, input: ArrayList<RData>): ArrayList<RData> {
        val output = ArrayList<RData>()
        val q = query.lowercase()

        input.forEach {
            if (it.title.startsWith(q)) output.add(it)
        }
        input.forEach {
            if (!output.contains(it) && it.title.contains(q)) output.add(it)
        }
        return output
    }
}