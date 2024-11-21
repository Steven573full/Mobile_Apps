package com.galartt.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import android.app.Application
import androidx.lifecycle.*
import com.galartt.data.GaleriaDao
import com.galartt.model.Galeria
import com.galartt.repository.GaleriaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SlideshowViewModel (application: Application): AndroidViewModel(application) {

    val getAllData: MutableLiveData<List<Galeria>>

    private val repository: GaleriaRepository = GaleriaRepository(GaleriaDao())

    init {
        getAllData = repository.getAllDataG
    }

    fun deleteArte(galeria: Galeria){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteArte(galeria)
        }
    }


}