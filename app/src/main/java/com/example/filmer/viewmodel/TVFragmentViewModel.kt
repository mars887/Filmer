package com.example.filmer.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.filmer.App
import com.example.filmer.data.FilmData
import com.example.filmer.domain.Interact
import javax.inject.Inject

class TVFragmentViewModel : ViewModel() {

    val filmListData = MutableLiveData<List<FilmData>>()

    @Inject
    lateinit var interactor: Interact

    init {
        App.instance.appComponent.inject(this)
        loadNewFilmList()
    }

    fun loadNewFilmList(onReload: Boolean = false) {
        if (System.currentTimeMillis() - interactor.lastApiRequest < interactor.RequestTimeout) {
            return
        }
        interactor.loadNewFilms(object : ApiCallback {
            override fun onSuccess() {
                filmListData.postValue(interactor.getFilmDataBase())
                Log.d("bebe", "loaded")
            }

            override fun onFailure() {
                filmListData.postValue(interactor.getFilmDataBase())
                Log.d("bebe", "failure")
            }
        },onReload)
    }

    fun reloadFilmList() {
        interactor.clearFilmList()
        loadNewFilmList(true)
    }

    interface ApiCallback {
        fun onSuccess()
        fun onFailure()
    }
}