package com.galartt.ui.galeria

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.galartt.R
import com.galartt.adapter.GaleriaAdapter
import com.galartt.databinding.FragmentGaleriaBinding
import com.galartt.viewmodel.GaleriaViewModel

// FragmentGaleriaBinding = FragmentArteBinding

class GaleriaFragment : Fragment() {

    private lateinit var galeriaViewModel: GaleriaViewModel

    private var _binding: FragmentGaleriaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        galeriaViewModel = ViewModelProvider(this)[GaleriaViewModel::class.java]
        _binding = FragmentGaleriaBinding.inflate(inflater, container,false)

        // accion para pasar a addObra
        binding.addArteButton.setOnClickListener{
            findNavController().navigate(R.id.action_nav_galeria_to_addArteFragment)
        }

        // activar el reciclador
        val galeriaAdapter = GaleriaAdapter()
        val reciclador = binding.reciclador

        reciclador.adapter = galeriaAdapter
        reciclador.layoutManager = LinearLayoutManager(requireContext())

        galeriaViewModel = ViewModelProvider(this)[GaleriaViewModel::class.java]

        galeriaViewModel.getAllData.observe(viewLifecycleOwner) {
            artes -> galeriaAdapter.setData(artes)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}