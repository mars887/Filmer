package com.example.filmer.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.filmer.App
import com.example.filmer.data.FilmData
import com.example.filmer.domain.Interact
import java.util.concurrent.Executors
import javax.inject.Inject

class TVFragmentViewModel : ViewModel() {

    var filmListData : LiveData<List<FilmData>>

    @Inject
    lateinit var interactor: Interact

    init {
        App.instance.appComponent.inject(this)
        filmListData = interactor.getFilmDataBase()
    }

    fun loadNewFilmList(onReload: Boolean = false,onSuccess: OnSuccess? = null,onFailure: OnFailure? = null) {
        if (System.currentTimeMillis() - interactor.lastApiRequest < interactor.RequestTimeout) {
            return
        }
            interactor.loadNewFilms(object : ApiCallback {
                override fun onSuccess() {
                    onSuccess?.onSuccess()
                    Log.d("bebe", "loaded")
                }

                override fun onFailure() {
                    onFailure?.onFailure()
                    Log.d("bebe", "failure")
                }
            },onReload)
    }

    interface ApiCallback {
        fun onSuccess()
        fun onFailure()
    }
    fun interface OnSuccess {
        fun onSuccess()
    }
    fun interface OnFailure {
        fun onFailure()
    }
}