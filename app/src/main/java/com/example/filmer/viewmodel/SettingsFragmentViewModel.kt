package com.example.filmer.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.filmer.App
import com.example.filmer.domain.Interact
import javax.inject.Inject

class SettingsFragmentViewModel : ViewModel() {
    //Инжектим интерактор
    @Inject
    lateinit var interactor: Interact
    val categoryPropertyLifeData: MutableLiveData<String> = MutableLiveData()

    init {
        App.instance.appComponent.inject(this)
        getCategoryProperty()
    }

    private fun getCategoryProperty() {
        categoryPropertyLifeData.value = interactor.getDefCategoryFP()
    }

    fun putCategoryProperty(category: String) {
        interactor.saveDefCategoryTP(category)
        getCategoryProperty()
    }
}