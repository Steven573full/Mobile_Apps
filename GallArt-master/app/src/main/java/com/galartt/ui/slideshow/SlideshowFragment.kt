package com.galartt.ui.slideshow

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

import com.galartt.databinding.FragmentSlideshowBinding
import com.galartt.viewmodel.SlideshowViewModel

// FragmentSlideshowBinding = FragmentArteBinding

class SlideshowFragment : Fragment() {

    private lateinit var slideshowViewModel: SlideshowViewModel

    private var _binding: FragmentSlideshowBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        slideshowViewModel = ViewModelProvider(this)[SlideshowViewModel::class.java]
        _binding = FragmentSlideshowBinding.inflate(inflater, container,false)
/*
        // accion para pasar a addObra
        binding.addArteButton.setOnClickListener{
            findNavController().navigate(R.id.action_nav_slideshow_to_addArteFragment)
        }
*/
        // activar el reciclador
        val slideshowAdapter = GaleriaAdapter()
        val reciclador = binding.reciclador

        reciclador.adapter = slideshowAdapter
        reciclador.layoutManager = LinearLayoutManager(requireContext())

        slideshowViewModel = ViewModelProvider(this)[SlideshowViewModel::class.java]

        slideshowViewModel.getAllData.observe(viewLifecycleOwner) {
                artes -> slideshowAdapter.setData(artes)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}