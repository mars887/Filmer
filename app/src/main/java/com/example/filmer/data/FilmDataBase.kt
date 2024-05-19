package com.example.filmer.data

import com.example.filmer.data.db.SQLInteractor
import com.example.filmer.data.sql.FavoriteFilmData
import com.example.filmer.data.sql.FilmDBDao
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.Executors
import javax.inject.Inject

class FilmDataBase @Inject constructor(
    private val sqlInteractor: SQLInteractor
) : DataBase<FilmData> {
    private var lastLoadedPage = 1
    private var onCategory: String? = null
    var lastLoadedFavorites: HashSet<FavoriteFilmData>? = null

    init {
        val disposable = sqlInteractor.getAllFavorites().subscribe {
            lastLoadedFavorites = it.toHashSet()
        }
    }

    fun getLastLoadedPage(category: String): Int {
        return if (category == onCategory) {
            lastLoadedPage
        } else {
            onCategory = category
            lastLoadedPage = 1
            1
        }
    }

    fun incrementLastLoadedPage() {
        lastLoadedPage++
    }

    fun resetPage() {
        lastLoadedPage = 1
    }

    override fun getFilmDB(): Observable<List<FilmData>> =
        sqlInteractor.getAllFilms()
            .map { list ->
                if (lastLoadedFavorites != null) {
                    list.forEach { film ->
                        val find = lastLoadedFavorites!!.find { it.title == film.title }
                        if (find != null && !film.isFavorite) {
                            film.isFavorite = true
                            sqlInteractor.addToFavorites(film)
                        }
                    }
                }
                list
            }


    override fun addFilms(films: List<FilmData>) {
        Executors.newSingleThreadExecutor().execute {
            sqlInteractor.addFilmsToDb(films)
        }
    }

    fun addFavorite(filmData: FilmData) = sqlInteractor.addToFavorites(filmData)
    fun removeFavorite(filmData: FilmData) = sqlInteractor.removeFromFavorites(filmData)
}