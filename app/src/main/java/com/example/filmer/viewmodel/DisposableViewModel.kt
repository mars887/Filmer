package com.example.filmer.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

open class DisposableViewModel: ViewModel() {
    val autoDisposable = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        autoDisposable.dispose()
    }
}

fun Disposable.bindTo(vmodel: DisposableViewModel) {
    vmodel.autoDisposable.add(this)
}
