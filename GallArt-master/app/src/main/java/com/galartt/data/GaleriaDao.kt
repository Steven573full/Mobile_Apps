package com.galartt.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.galartt.model.Galeria
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.ktx.Firebase

class GaleriaDao {

    private val coleccion1="gallartApp"
    private val usuario= Firebase.auth.currentUser?.email.toString()
    private val coleccion2="misArtes"

    //Obtener la instancia de la BD en Firestore
    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    //Inicializamos la instancia
    init{
        firestore.firestoreSettings= FirebaseFirestoreSettings.Builder().build()
    }

    //Mutable = Cambiar en tiempo real
    fun getAllData(): MutableLiveData<List<Galeria>> {
        val listaArtes = MutableLiveData<List<Galeria>>()

        //Recuperar todos los "documentos" / "galerias" de nuestra colección
        firestore.collection(coleccion1).document(usuario).collection(coleccion2)
            .addSnapshotListener{
                    instantanea, e ->
                if(e != null){
                    return@addSnapshotListener
                }
                if(instantanea != null){ //Si hay info y se recupera los datos
                    val lista = ArrayList<Galeria>()
                    //Se recorre la instantanea
                    instantanea.documents.forEach{
                        val galeria = it.toObject(Galeria::class.java)
                        if(galeria!=null){
                            lista.add(galeria)
                        }
                    }
                    listaArtes.value = lista
                }
            }

        return listaArtes
    }

    fun getAllDataG(): MutableLiveData<List<Galeria>> {
        val listaArtes = MutableLiveData<List<Galeria>>()

        //Recuperar todos los "documentos" / "galerias" de nuestra colección
        firestore.collection(coleccion1).document("compartido@gmail.com").collection(coleccion2)
            .addSnapshotListener{
                    instantanea, e ->
                if(e != null){
                    return@addSnapshotListener
                }
                if(instantanea != null){ //Si hay info y se recupera los datos
                    val lista = ArrayList<Galeria>()
                    //Se recorre la instantanea
                    instantanea.documents.forEach{
                        val galeria = it.toObject(Galeria::class.java)
                        if(galeria!=null){
                            lista.add(galeria)
                        }
                    }
                    listaArtes.value = lista
                }
            }

        return listaArtes
    }

    fun saveArte(galeria: Galeria){

        val documento : DocumentReference

        if(galeria.id.isEmpty()){ //Si no hay id entonces es un ADD
            documento = firestore.collection(coleccion1).document(usuario).
            collection(coleccion2).document()
            //En el error lo cambiamos a var
            galeria.id = documento.id

        }else{ //Significa que el arte existe... entonces lo voy a modificar
            documento = firestore.collection(coleccion1).document(usuario).
            collection(coleccion2).document(galeria.id)
        }

        documento.set(galeria)
            .addOnSuccessListener {
                Log.d("saveArte", "Obra de arte agregada/modificada")
            }
            .addOnCanceledListener {
                Log.d("saveArte", "ERROR Obra de arte NO agregada/modificada")
            }
    }

    fun saveArteG(galeria: Galeria){

        val documento : DocumentReference

        if(galeria.id.isEmpty()){ //Si no hay id entonces es un ADD
            documento = firestore.collection(coleccion1).document("compartido@gmail.com").
            collection(coleccion2).document()
            //En el error lo cambiamos a var
            galeria.id = documento.id

        }else{ //Significa que el arte existe... entonces lo voy a modificar
            documento = firestore.collection(coleccion1).document("compartido@gmail.com").
            collection(coleccion2).document(galeria.id)
        }

        documento.set(galeria)
            .addOnSuccessListener {
                Log.d("saveArte", "Obra de arte agregada/modificada")
            }
            .addOnCanceledListener {
                Log.d("saveArte", "ERROR Obra de arte NO agregada/modificada")
            }
    }


    fun deleteArte(galeria: Galeria){
        if(galeria.id.isNotEmpty()){ //El arte existe
            firestore.collection(coleccion1).document(usuario).
            collection(coleccion2).document(galeria.id).delete()

                .addOnSuccessListener {
                    Log.d("deleteArte", "Arte Eliminado")
                }
                .addOnCanceledListener {
                    Log.d("deleteArte", "ERROR Arte NO Eliminado")
                }
        }
    }
}