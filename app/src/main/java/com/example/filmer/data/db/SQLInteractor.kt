package com.example.filmer.data.db

import com.example.filmer.data.FilmData
import com.example.filmer.data.sql.FavoriteFilmData
import com.example.filmer.data.sql.FavoritesDBDao
import com.example.filmer.data.sql.FilmDBDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

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
            favoritesDBDao.insertFavorite(FavoriteFilmData(filmData))
            filmDBDao.insertFilm(filmData)
        }
    }

    fun removeFromFavorites(filmData: FilmData) {
        scope.launch {
            filmData.isFavorite = false
            favoritesDBDao.deleteFavorite(FavoriteFilmData(filmData))
            filmDBDao.insertFilm(filmData)
        }
    }
}