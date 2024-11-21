package com.lugares.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lugares.databinding.LugarFilaBinding
import com.lugares.model.Lugar
import com.lugares.ui.lugar.LugarFragmentDirections

class LugarAdapter : RecyclerView.Adapter<LugarAdapter.LugarViewHolder>(){

    //Una lista para almacenar la información de los lugares
    private var listaLugares = emptyList<Lugar>()

    //Creamos la clase interna LugarViewHolder
    inner class LugarViewHolder (private val itemBinding: LugarFilaBinding) :
        RecyclerView.ViewHolder(itemBinding.root)
    {
        //Funcion que recibe un objeto lugar y se lo asigna a los elemento del cardView
        fun dibuja(lugar: Lugar){
            itemBinding.tvNombre.text = lugar.nombre
            itemBinding.tvCorreo.text = lugar.correo
            itemBinding.tvTelefono.text = lugar.telefono
            itemBinding.tvWeb.text = lugar.web

            Glide.with(itemBinding.root.context)
                .load(lugar.rutaImagen)
                .circleCrop()
                .into(itemBinding.imagen)
            itemBinding.vistaFila.setOnClickListener{
                val accion =
                    LugarFragmentDirections.actionNavLugarToUpdateLugarFragment(lugar)
                itemView.findNavController().navigate(accion)
            }
        }
    }

    //Metodo para crear una card
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LugarViewHolder {
        val itemBinding =
            LugarFilaBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)

        return LugarViewHolder(itemBinding)
    }

    //Una vez creada la tarjeta, tomamos la info para ponerla en el card
    override fun onBindViewHolder(holder: LugarViewHolder, position: Int) {
        val lugar = listaLugares[position]
        holder.dibuja(lugar)
    }

    override fun getItemCount(): Int {
        return listaLugares.size
    }

    fun setData(lugares: List<Lugar>){
        this.listaLugares=lugares
        notifyDataSetChanged()
    }
}