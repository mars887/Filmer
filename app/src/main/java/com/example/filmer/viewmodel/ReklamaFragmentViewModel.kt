package com.example.filmer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.filmer.usecases.CheckPosterStateUseCase
import com.example.sql_module.FilmData

class ReklamaFragmentViewModel(
    val film: FilmData,
) : ViewModel() {



    class Factory(
        private val film: FilmData
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ReklamaFragmentViewModel::class.java)) {
                return ReklamaFragmentViewModel(
                    film
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
