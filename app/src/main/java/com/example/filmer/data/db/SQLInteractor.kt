package com.example.filmer.data.db

import com.example.sql_module.FilmData
import com.example.sql_module.sql.FavoritesDBDao
import com.example.sql_module.sql.FilmDBDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class SQLInteractor @Inject constructor(
    private val filmDBDao: FilmDBDao,
    private val favoritesDBDao: FavoritesDBDao,
    private val scope: CoroutineScope
) {

    fun addFilmsToDb(films: List<FilmData>) {
        filmDBDao.insertAll(films)
    }

    fun setNewFilmsListToDb(films: List<FilmData>) {
        filmDBDao.deleteAll()
        filmDBDao.insertAll(films)
    }

    fun getAllFilms() = filmDBDao.getAllFilms()

    fun getAllFavorites() = favoritesDBDao.getAllFavorites()

    fun addToFavorites(filmData: FilmData) {
        scope.launch {
            filmData.isFavorite = true
            favoritesDBDao.insertFavorite(filmData.toFavorite())
            filmDBDao.insertFilm(filmData)
        }
    }

    fun removeFromFavorites(filmData: FilmData) {
        scope.launch {
            filmData.isFavorite = false
            favoritesDBDao.deleteFavorite(filmData.toFavorite())
            filmDBDao.insertFilm(filmData)
        }
    }

    fun getFilmByTitle(title: String) = filmDBDao.getFilmByTitle(title)
}