package com.example.filmer.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.filmer.App
import com.example.filmer.data.FilmData
import com.example.filmer.domain.Interactor

class TVFragmentViewModel : ViewModel() {

    val filmListData = MutableLiveData<List<FilmData>>()

    private var interactor: Interactor = App.instance.interactor

    init {
        val films = interactor.getFilmDataBase()
        filmListData.postValue(films)
    }

    fun loadNewFilmList() {
        interactor.loadNewFilms(object : ApiCallback {
            override fun onSuccess() {
                filmListData.postValue(interactor.getFilmDataBase())
                Log.d("bebe", "loaded")
            }

            override fun onFailure() {
                Log.d("bebe", "failure")
            }
        })
    }

    interface ApiCallback {
        fun onSuccess()
        fun onFailure()
    }
}