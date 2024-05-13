package com.example.filmer.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.filmer.App
import com.example.filmer.data.FilmData
import com.example.filmer.domain.Interact
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.Executors
import javax.inject.Inject

class TVFragmentViewModel : ViewModel() {

    var filmListData: Flow<List<FilmData>>

    @Inject
    lateinit var interactor: Interact
    val showProgressBar: Channel<Boolean>

    init {
        App.instance.appComponent.inject(this)
        showProgressBar = interactor.progressBarState
        filmListData = interactor.getFilmDataBase()
        loadNewFilmList()
    }

    fun loadNewFilmList(onReload: Boolean = false) {
        if (System.currentTimeMillis() - interactor.lastApiRequest < interactor.RequestTimeout) {
            return
        }
        interactor.loadNewFilms(onReload)
    }
}