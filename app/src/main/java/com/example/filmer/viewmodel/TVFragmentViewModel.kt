package com.example.filmer.viewmodel

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

}