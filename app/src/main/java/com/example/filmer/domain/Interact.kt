package com.example.filmer.domain

import com.example.filmer.data.FilmData
import com.example.filmer.data.FilmDataBase
import com.example.filmer.data.PreferenceProvider
import com.example.filmer.data.api.ApiQResult
import com.example.filmer.data.api.FilmApi
import com.example.filmer.data.api.FilmApiKey
import com.example.filmer.data.api.ResultToFilmsConverter
import com.example.filmer.data.db.SQLInteractor
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class Interact @Inject constructor(
    private val dbase: FilmDataBase,
    private val filmApi: FilmApi,
    private val preferences: PreferenceProvider,
    private val sqlInteractor: SQLInteractor
) {
    var lastApiRequest = 0L
    val RequestTimeout = 500

    var progressBarState: BehaviorSubject<Boolean> = BehaviorSubject.create()

    fun getFilmDataBase(): Observable<List<FilmData>> = dbase.getFilmDB()

    fun loadNewFilms(onReload: Boolean = false) {
        lastApiRequest = System.currentTimeMillis()
        val currCategory = getDefCategoryFP()
        if (onReload) dbase.resetPage()

        filmApi.getFilms(
            currCategory,
            FilmApiKey.FILM_API_KEY,
            "ru",
            dbase.getLastLoadedPage(currCategory)
        )
            .enqueue(object : Callback<ApiQResult> {
                override fun onResponse(call: Call<ApiQResult>, response: Response<ApiQResult>) {
                    try {
                        val data =
                            ResultToFilmsConverter.convertToFilmsList(response.body()?.results!!)
                        dbase.incrementLastLoadedPage()

                        Completable.fromSingle<List<FilmData>> {
                            if (onReload && data.isNotEmpty())
                                sqlInteractor.setNewFilmsListToDb(data)
                            else if (data.isNotEmpty())
                                sqlInteractor.addFilmsToDb(data)
                            progressBarState.onNext(false)
                        }
                            .subscribeOn(Schedulers.io())
                            .subscribe()

                    } catch (_: Exception) {
                        progressBarState.onNext(false)
                    }
                }

                override fun onFailure(call: Call<ApiQResult>, t: Throwable) {
                    progressBarState.onNext(false)

                }
            })
    }

    fun getDefCategoryFP() = preferences.getDefaultCategory()
    fun saveDefCategoryTP(category: String): Boolean {
        return if (category != preferences.getDefaultCategory()) {
            preferences.saveDefaultCategory(category)
            true
        } else false
    }
}
