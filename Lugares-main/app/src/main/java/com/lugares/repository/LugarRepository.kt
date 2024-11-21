package com.lugares.repository

import androidx.lifecycle.MutableLiveData
import com.lugares.data.LugarDao
import com.lugares.model.Lugar

class LugarRepository (private val lugarDao: LugarDao){
    //Se implementan las funciones de la interface

    //Live data es un observador que se encarga de estar pendiente si el array list es modificado
    val getAllData: MutableLiveData<List<Lugar>> = lugarDao.getAllData()

    //Se define la función para insertar un lugar en la coleccion misLugares
    fun saveLugar(lugar:Lugar){
        lugarDao.saveLugar(lugar)
    }

    //Se define la función para eliminar un lugar en la en la conexion misLugares
     fun deleteLugar(lugar:Lugar){
        lugarDao.deleteLugar(lugar)
    }
}