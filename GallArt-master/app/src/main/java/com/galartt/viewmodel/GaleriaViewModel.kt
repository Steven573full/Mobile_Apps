package com.galartt.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.galartt.data.GaleriaDao
import com.galartt.model.Galeria
import com.galartt.repository.GaleriaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GaleriaViewModel (application: Application): AndroidViewModel(application) {

    val getAllData: MutableLiveData<List<Galeria>>

    private val repository: GaleriaRepository = GaleriaRepository(GaleriaDao())

    init {
        getAllData = repository.getAllData
    }

    fun saveArte(galeria: Galeria){
        viewModelScope.launch(Dispatchers.IO){
            repository.saveArte(galeria)
        }
    }

    fun saveArteG(galeria: Galeria){
        viewModelScope.launch(Dispatchers.IO){
            repository.saveArteG(galeria)
        }
    }

    fun deleteArte(galeria: Galeria){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteArte(galeria)
        }
    }

}