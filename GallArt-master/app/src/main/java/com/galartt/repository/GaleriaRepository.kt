package com.galartt.repository

import androidx.lifecycle.MutableLiveData
import com.galartt.data.GaleriaDao
import com.galartt.model.Galeria

class GaleriaRepository (private val galeriaDao: GaleriaDao) {

    val getAllData: MutableLiveData<List<Galeria>> = galeriaDao.getAllData()
    val getAllDataG: MutableLiveData<List<Galeria>> = galeriaDao.getAllDataG()

    fun saveArte(galeria: Galeria){
        galeriaDao.saveArte(galeria)
    }

    fun saveArteG(galeria: Galeria){
        galeriaDao.saveArteG(galeria)
    }

    fun deleteArte(galeria: Galeria){
        galeriaDao.deleteArte(galeria)
    }
}