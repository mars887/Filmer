package com.example.filmer.data.apiUtils

import com.example.sql_module.FilmData

object ResultToFilmsConverter {
    fun convertToFilmsList(list: List<com.example.remote_module.entity.FilmResult>): List<FilmData> {
        val result = mutableListOf<FilmData>()

        list.forEach {
            result.add(
                FilmData(
                    id = it.id,
                    poster = it.poster_path,
                    title = it.title,
                    description = it.overview,
                    rating = it.vote_average,
                    isFavorite = false,
                )
            )
        }
        return result
    }
}