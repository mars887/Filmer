package com.example.filmer.domain

import com.example.sql_module.FilmData
import com.example.filmer.data.FilmDataBase
import com.example.filmer.data.PreferenceProvider
import com.example.filmer.data.apiUtils.ResultToFilmsConverter
import com.example.filmer.data.db.SQLInteractor
import com.example.remote_module.FilmApi
import com.example.remote_module.entity.FilmApiKey
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class Interact @Inject constructor(
    val dbase: FilmDataBase,
    private val filmApi: FilmApi,
    private val preferences: PreferenceProvider,
    private val sqlInteractor: SQLInteractor
) {
    var lastApiRequest = 0L
    val RequestTimeout = 500

    var progressBarState: BehaviorSubject<Boolean> = BehaviorSubject.create()

    fun getFilmDataBase(): Observable<List<FilmData>> = dbase.getFilmDB()

    fun searchNewFilms(query: String, onReload: Boolean = false) {
        lastApiRequest = System.currentTimeMillis()
        if (onReload) dbase.resetPage()


        val disposable = filmApi.getFilmsSearch(
            FilmApiKey.FILM_API_KEY,
            "ru",
            query.lowercase(),
            dbase.getLastLoadedPage("querry_$query")
        ).map {
            ResultToFilmsConverter.convertToFilmsList((it.results))
        }.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe({ data ->
                if (data.isNotEmpty())
                    dbase.incrementLastLoadedPage()

                if (dbase.getLastLoadedPage("querry_$query") == 1 || onReload) {
                    sqlInteractor.setNewFilmsListToDb(data)
                } else sqlInteractor.addFilmsToDb(data)

                progressBarState.onNext(false)
            }, {
                progressBarState.onNext(false)
            })
    }

    fun loadNewFilms(onReload: Boolean = false) {
        lastApiRequest = System.currentTimeMillis()
        val currCategory = getDefCategoryFP()
        if (onReload) dbase.resetPage()

        val disposable = filmApi.getFilms(
            currCategory,
            FilmApiKey.FILM_API_KEY,
            "ru",
            dbase.getLastLoadedPage(currCategory)
        ).map {
            ResultToFilmsConverter.convertToFilmsList((it.results))
        }.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe({ data ->

                if (onReload && data.isNotEmpty())
                    sqlInteractor.setNewFilmsListToDb(data)
                else if (data.isNotEmpty()) {
                    sqlInteractor.addFilmsToDb(data)
                    dbase.incrementLastLoadedPage()
                }

                progressBarState.onNext(false)
            }, {
                progressBarState.onNext(false)
            })
    }

    suspend fun getFilmById(filmId: Long): Flow<FilmData> {
        val flow = MutableSharedFlow<FilmData>(replay = 1)
        dbase.getFilmById(filmId).collect {
            if (it != null) {
                flow.emit(it)
            } else {
                loadFilmById(filmId).collect {
                    flow.emit(it)
                }
            }
        }
        return flow
    }

    suspend fun loadFilmById(filmId: Long): Flow<FilmData> {
        val flow = MutableSharedFlow<FilmData>(replay = 1)
        val disposable = filmApi.getFilmById(
            filmId.toString(),
            FilmApiKey.FILM_API_KEY,
            "ru"
        ).map {
            ResultToFilmsConverter.convertMovieResultToFilmData(it)
        }.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe {
                sqlInteractor.addOneFilm(it)
                runBlocking {
                    flow.emit(it)
                }
            }
        return flow
    }

    fun getDefCategoryFP() = preferences.getDefaultCategory()
    fun saveDefCategoryTP(category: String): Boolean {
        return if (category != preferences.getDefaultCategory()) {
            preferences.saveDefaultCategory(category)
            true
        } else false
    }

    fun getAppTheme() = preferences.getAppTheme()
    fun setAppTheme(appTheme: String) {
        preferences.saveAppTheme(appTheme)
    }

    fun checkTrialState(): Boolean = preferences.trialState!!

    fun getFilmByTitle(title: String): Observable<FilmData> = sqlInteractor.getFilmByTitle(title)
}
