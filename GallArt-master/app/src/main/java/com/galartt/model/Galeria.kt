package com.galartt.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Galeria(
    var id: String,
    val autor:String,
    val correo: String?,
    val telefono: String?,
    val web: String?,
    val latitud: Double?,
    val altura: Double?,
    val longitud: Double?,
    val rutaAudio: String?,
    val rutaImagen: String?,
): Parcelable{
    constructor():this("","","","","",0.0,0.0,0.0,"","")
}
