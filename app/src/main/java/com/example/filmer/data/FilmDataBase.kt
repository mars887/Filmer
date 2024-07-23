package com.example.filmer.data

import com.example.filmer.data.db.SQLInteractor
import com.example.filmer.domain.Interact
import com.example.sql_module.FilmData
import com.example.sql_module.sql.FavoriteFilmData
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.internal.ChannelFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.Executors
import javax.inject.Inject

class FilmDataBase @Inject constructor(
    private val sqlInteractor: SQLInteractor
) : DataBase<FilmData> {
    private var lastLoadedPage = 1
    private var onCategory: String? = null
    var lastLoadedFavorites: HashSet<FilmData>? = null

    init {
        val disposable = sqlInteractor.getAllFavorites().subscribe {
            lastLoadedFavorites = it.map {
                it.toFilmData()
            }.toHashSet()
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

    override fun getFilmById(id: Long) = channelFlow<FilmData?> {
        val mutex = Mutex(true)
        val disposable = getFilmDB().subscribe {
            val find = it.find {
                it.id == id.toInt()
            }
            runBlocking {
                send(find)
                mutex.unlock()
            }
        }
        mutex.withLock {
            disposable.dispose()
        }
    }


    override fun addFilms(films: List<FilmData>) {
        Executors.newSingleThreadExecutor().execute {
            sqlInteractor.addFilmsToDb(films)
        }
    }

    fun addFavorite(filmData: FilmData) = sqlInteractor.addToFavorites(filmData)
    fun removeFavorite(filmData: FilmData) = sqlInteractor.removeFromFavorites(filmData)
}