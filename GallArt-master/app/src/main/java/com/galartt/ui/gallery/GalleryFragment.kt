package com.galartt.ui.gallery

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.galartt.databinding.FragmentGalleryBinding
import com.galartt.model.Galeria
import com.galartt.viewmodel.GaleriaViewModel
import com.galartt.viewmodel.GalleryViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class GalleryFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentGalleryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //El objeto para interactuar con la vista (mapa)
    private lateinit var googleMap: GoogleMap
    private var mapReady = false

    private lateinit var galeriaViewModel: GaleriaViewModel

    //Esto se ejecuta una vez cuando el activity se crea...
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //Se solicita la actualización del mapa
        binding.map.onCreate(savedInstanceState)
        binding.map.onResume()
        binding.map.getMapAsync(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        galeriaViewModel =
            ViewModelProvider(this).get(GaleriaViewModel::class.java)

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(map: GoogleMap) {
        map.let {
            googleMap = it
            mapReady = true
            // se instruye al mapa para que se vean los lugares, segun coordenadas
            galeriaViewModel.getAllData.observe(viewLifecycleOwner) {
                    artes ->
                updateMap(artes)
                ubicaCamara()
            }
        }
    }

    private fun ubicaCamara() {
        val ubicacion: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {  //Si no tengo los permisos... entonces pido los permisos
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION),105)
        } else {  //Tengo los permisos entonces recupero las coordenadas...
            ubicacion.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location!=null) { //Se pudo leer las coordenadas gps...
                        val camaraUpdate = CameraUpdateFactory.newLatLngZoom(
                            LatLng(location.latitude,location.longitude),15f)
                        googleMap.animateCamera(camaraUpdate)
                    }
                }
        }
    }

    private fun updateMap(artes: List<Galeria>) {
        if (mapReady) {
            artes.forEach { arte ->
                if (arte.latitud?.isFinite() == true && arte.longitud?.isFinite() == true) {
                    val marca = LatLng(arte.latitud,arte.longitud)
                    googleMap.addMarker(MarkerOptions().position(marca).title(arte.autor))
                }
            }
        }
    }

}