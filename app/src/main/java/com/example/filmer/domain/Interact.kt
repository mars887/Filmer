package com.example.filmer.domain

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.filmer.data.api.ApiQResult
import com.example.filmer.data.api.FilmApi
import com.example.filmer.data.api.FilmApiKey
import com.example.filmer.data.FilmData
import com.example.filmer.data.FilmDataBase
import com.example.filmer.data.PreferenceProvider
import com.example.filmer.data.api.ResultToFilmsConverter
import com.example.filmer.data.db.SQLInteractor
import com.example.filmer.viewmodel.TVFragmentViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors
import javax.inject.Inject

class Interact @Inject constructor(
    private val dbase: FilmDataBase,
    private val filmApi: FilmApi,
    private val preferences: PreferenceProvider,
    private val sqlInteractor: SQLInteractor
) {
    var lastApiRequest = 0L
    val RequestTimeout = 500

    val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    var progressBarState = Channel<Boolean>(Channel.CONFLATED)

    fun getFilmDataBase(): Flow<List<FilmData>> = dbase.getFilmDB()

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

                        scope.launch {
                            if (onReload && data.isNotEmpty())
                                sqlInteractor.setNewFilmsListToDb(data)
                            else if (data.isNotEmpty())
                                sqlInteractor.addFilmsToDb(data)
                            progressBarState.send(false)
                        }

                    } catch (_: Exception) {
                    } finally {
                        scope.launch {
                            progressBarState.send(false)
                        }
                    }
                }

                override fun onFailure(call: Call<ApiQResult>, t: Throwable) {
                    scope.launch {
                        progressBarState.send(false)
                    }
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
