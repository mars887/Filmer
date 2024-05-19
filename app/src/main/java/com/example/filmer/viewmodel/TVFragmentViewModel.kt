package com.example.filmer.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.filmer.App
import com.example.filmer.data.FilmData
import com.example.filmer.domain.Interact
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.Executors
import javax.inject.Inject

class TVFragmentViewModel : ViewModel() {

    var filmListData: Observable<List<FilmData>>

    @Inject
    lateinit var interactor: Interact
    val showProgressBar: BehaviorSubject<Boolean>
    val autoDisposable = CompositeDisposable()

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
        println("loading $onReload")
        interactor.loadNewFilms(onReload)
    }

    fun searchNewFilmList(query: String,onReload: Boolean = false) {
        if (System.currentTimeMillis() - interactor.lastApiRequest < interactor.RequestTimeout) {
            return
        }
        println("searching")
        interactor.searchNewFilms(query,onReload)
    }

    override fun onCleared() {
        super.onCleared()
        autoDisposable.dispose()
    }

}

fun Disposable.bindTo(tvvmodel: TVFragmentViewModel) {
    tvvmodel.autoDisposable.add(this)
}
