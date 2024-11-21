package com.lugares.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.lugares.data.LugarDao
import com.lugares.model.Lugar
import com.lugares.repository.LugarRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//Creamos una clase con un objeto de tipo aplicación, además de heredar
class LugarViewModel(application: Application): AndroidViewModel(application) {

    //se hace lo mismo que en LugarRepository, pero esto es una capa superior
    val getAllData: MutableLiveData<List<Lugar>>

    //Necesitamos acceso al repositorio
    private val repository: LugarRepository = LugarRepository(LugarDao())

    //Iniciamos las variables anteriormente creadas
    init {getAllData = repository.getAllData}

    //Llama al subproceso del SO y agrega un objeto lugar a la BD
    //Función de alto nivel
    fun saveLugar(lugar: Lugar){
        //Lanzar un hilo para que realiza una tarea asincrona
        viewModelScope.launch(Dispatchers.IO){
            repository.saveLugar(lugar)
        }
    }

    //Llama al subproceso del SO y actualiza un objeto lugar a la BD
    //Función de alto nivel
    fun updateLugar(lugar: Lugar){
        //Lanzar un hilo para que realiza una tarea asincrona
        viewModelScope.launch(Dispatchers.IO){
            repository.saveLugar(lugar)
        }
    }
    //Llama al subproceso del SO y elimina un objeto lugar de la BD
    //Función de alto nivel
    fun deleteLugar(lugar: Lugar){
        //Lanzar un hilo para que realiza una tarea asincrona
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteLugar(lugar)
        }
    }
}