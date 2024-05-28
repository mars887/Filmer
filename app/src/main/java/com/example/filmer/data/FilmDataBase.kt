package com.example.filmer.data

import com.example.filmer.data.db.SQLInteractor
import com.example.sql_module.sql.FavoriteFilmData
import io.reactivex.rxjava3.core.Observable
import java.util.concurrent.Executors
import javax.inject.Inject

class FilmDataBase @Inject constructor(
    private val sqlInteractor: SQLInteractor
) : DataBase<com.example.sql_module.FilmData> {
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

    override fun getFilmDB(): Observable<List<com.example.sql_module.FilmData>> =
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


    override fun addFilms(films: List<com.example.sql_module.FilmData>) {
        Executors.newSingleThreadExecutor().execute {
            sqlInteractor.addFilmsToDb(films)
        }
    }

    fun addFavorite(filmData: com.example.sql_module.FilmData) = sqlInteractor.addToFavorites(filmData)
    fun removeFavorite(filmData: com.example.sql_module.FilmData) = sqlInteractor.removeFromFavorites(filmData)
}