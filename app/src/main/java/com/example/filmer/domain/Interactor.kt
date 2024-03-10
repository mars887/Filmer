package com.example.filmer.domain

import android.util.Log
import com.example.filmer.data.api.ApiQResult
import com.example.filmer.data.api.FilmApi
import com.example.filmer.data.api.FilmApiKey
import com.example.filmer.data.FilmData
import com.example.filmer.data.FilmDataBase
import com.example.filmer.data.api.ResultToFilmsConverter
import com.example.filmer.viewmodel.TVFragmentViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Interactor(private val dbase: FilmDataBase, private val filmApi: FilmApi) {
    private var lastApiRequest = 0L
    private val RequestTimeout = 200

    fun getFilmDataBase(): List<FilmData> = dbase.getAll()

    fun loadNewFilms(callback: TVFragmentViewModel.ApiCallback) {
        if (System.currentTimeMillis() - lastApiRequest < RequestTimeout) {
            callback.onFailure()
            return
        }
        filmApi.getPopular(FilmApiKey.FILM_API_KEY, "ru", dbase.getLastLoadedPage())
            .enqueue(object : Callback<ApiQResult> {
                override fun onResponse(call: Call<ApiQResult>, response: Response<ApiQResult>) {
                    try {
                        dbase.addFilms(ResultToFilmsConverter.convertToFilmsList(response.body()?.results!!))
                        callback.onSuccess()
                        dbase.incrementLastLoadedPage()
                        lastApiRequest = System.currentTimeMillis()
                    } catch (e: Exception) {
                        callback.onFailure()
                    }
                }

                override fun onFailure(call: Call<ApiQResult>, t: Throwable) {
                    callback.onFailure()
                    Log.d("bebe",t.toString())
                }
            })
    }
}
