package com.lugares.ui.lugar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.lugares.R
import com.lugares.adapter.LugarAdapter
import com.lugares.databinding.FragmentAddLugarBinding
import com.lugares.databinding.FragmentLugarBinding
import com.lugares.viewmodel.LugarViewModel

class LugarFragment : Fragment() {

    //Variable para acceder a la parte de datos BD
    private lateinit var lugarViewModel: LugarViewModel

    //Accedemos a la parte visual de addLugar
    private var _binding: FragmentLugarBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        lugarViewModel = ViewModelProvider(this)[LugarViewModel::class.java]
        _binding = FragmentLugarBinding.inflate(inflater, container, false)

        //Se programa la accion para pasarse a addLugar
        binding.addLugarButton.setOnClickListener{
            //Este control de navegación son las flechas que dirigen las pantallas
            //Navegamos de lugar a addLugar
            findNavController().navigate(R.id.action_nav_lugar_to_addLugarFragment)
        }

        //Activar el Reciclador
        val lugarAdapter = LugarAdapter()
        val reciclador = binding.reciclador
        //Pasamos el adaptadpr al reciclador
        reciclador.adapter = lugarAdapter
        //Hacemos un recicleView
        reciclador.layoutManager = LinearLayoutManager(requireContext())
        //Accedemos al lugarViewModel que tiene el acceso a los datos de la tabla
        lugarViewModel = ViewModelProvider(this)[LugarViewModel::class.java]

        lugarViewModel.getAllData.observe(viewLifecycleOwner){
            lugares -> lugarAdapter.setData(lugares)
        }
        return binding.root
    }

    //Cuando el fragmento se deja de ver, se libera la memoria
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}