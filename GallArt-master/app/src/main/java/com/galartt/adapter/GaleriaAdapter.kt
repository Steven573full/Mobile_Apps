package com.galartt.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.galartt.databinding.ArteFilaBinding
import com.galartt.model.Galeria
import com.galartt.ui.galeria.GaleriaFragmentDirections

class GaleriaAdapter: RecyclerView.Adapter<GaleriaAdapter.GaleriaViewHolder>() {

    // lista para lamacenar la info de las artes
    private var listaArtes = emptyList<Galeria>()

    inner class GaleriaViewHolder(private  val itemBinding: ArteFilaBinding) :
    RecyclerView.ViewHolder(itemBinding.root){
        fun dibuja(galeria: Galeria) {
            itemBinding.tvAutor.text = galeria.autor
            itemBinding.tvCorreo.text = galeria.correo
            itemBinding.tvTelefono.text = galeria.telefono
            itemBinding.tvWeb.text = galeria.web
            Glide.with(itemBinding.root.context)
                .load(galeria.rutaImagen)
                .circleCrop()
                .into(itemBinding.imagen)

            itemBinding.vistaFila.setOnClickListener {
                val accion = GaleriaFragmentDirections
                    .actionNavGaleriaToUpdateArteFragment(galeria)
                itemView.findNavController().navigate(accion)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GaleriaViewHolder {
        val itemBinding =
            ArteFilaBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        return GaleriaViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: GaleriaViewHolder, position: Int) {
        val arte = listaArtes[position]
        holder.dibuja(arte)
    }

    override fun getItemCount(): Int {
        return listaArtes.size
    }

    fun setData(artes: List<Galeria>) {
        this.listaArtes = artes
        // se redibuja toda la lista
        notifyDataSetChanged()
    }
}