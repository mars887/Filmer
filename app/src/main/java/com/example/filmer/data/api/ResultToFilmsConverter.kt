package com.example.filmer.data.api

import com.example.filmer.data.FilmData

object ResultToFilmsConverter {
    fun convertToFilmsList(list: List<FilmResult>): List<FilmData> {
        val result = mutableListOf<FilmData>()

        list.forEach {
            result.add(
                FilmData(
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