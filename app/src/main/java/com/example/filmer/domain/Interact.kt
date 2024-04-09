package com.example.filmer.domain

import android.util.Log
import com.example.filmer.data.api.ApiQResult
import com.example.filmer.data.api.FilmApi
import com.example.filmer.data.api.FilmApiKey
import com.example.filmer.data.FilmData
import com.example.filmer.data.FilmDataBase
import com.example.filmer.data.PreferenceProvider
import com.example.filmer.data.api.ResultToFilmsConverter
import com.example.filmer.data.db.SQLInteractor
import com.example.filmer.viewmodel.TVFragmentViewModel
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

    fun getFilmDataBase(): List<FilmData> = dbase.getAll()

    fun clearFilmList() {
        dbase.clearBase()
    }

    fun loadNewFilms(callback: TVFragmentViewModel.ApiCallback, onReload: Boolean = false) {
        Log.d("bebe","loading new films...")
        lastApiRequest = System.currentTimeMillis()
        val currCategory = getDefCategoryFP()

        filmApi.getFilms(
            currCategory,
            FilmApiKey.FILM_API_KEY,
            "ru",
            dbase.getLastLoadedPage(currCategory)
        )
            .enqueue(object : Callback<ApiQResult> {
                override fun onResponse(call: Call<ApiQResult>, response: Response<ApiQResult>) {
                    val sqlDataSize = sqlInteractor.getDbSize()
                    try {
                        val data =
                            ResultToFilmsConverter.convertToFilmsList(response.body()?.results!!)
                        dbase.incrementLastLoadedPage()
                        dbase.addFilms(data)
                        if (onReload && data.isNotEmpty())
                            sqlInteractor.setNewFilmsListToDb(data)
                        else if(data.isNotEmpty())
                            sqlInteractor.addFilmsToDb(data)
                        callback.onSuccess()
                    } catch (e: Exception) {
                        if (sqlDataSize > dbase.getSize()) {
                            dbase.clearBase()
                            dbase.addFilms(sqlInteractor.getAllFromDB())
                        }
                        callback.onFailure()
                        Log.d("bebe",e.message ?: "")
                    }
                }

                override fun onFailure(call: Call<ApiQResult>, t: Throwable) {
                    if (sqlInteractor.getDbSize() > dbase.getSize()) {
                        dbase.clearBase()
                        dbase.addFilms(sqlInteractor.getAllFromDB())
                    }
                    callback.onFailure()
                    Log.d("bebe",t.message ?: "")
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
