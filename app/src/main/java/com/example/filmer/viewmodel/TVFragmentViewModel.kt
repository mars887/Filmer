package com.example.filmer.viewmodel

import androidx.lifecycle.ViewModel
import com.example.filmer.App
import com.example.sql_module.FilmData
import com.example.filmer.domain.Interact
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject

class TVFragmentViewModel : DisposableViewModel() {

    var filmListData: Observable<List<FilmData>>

    @Inject
    lateinit var interactor: Interact
    val showProgressBar: BehaviorSubject<Boolean>

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
}