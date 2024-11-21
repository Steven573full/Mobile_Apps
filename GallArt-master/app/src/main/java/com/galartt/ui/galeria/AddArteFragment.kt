package com.galartt.ui.galeria

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.galartt.R
import com.galartt.databinding.FragmentAddArteBinding
import com.galartt.model.Galeria
import com.galartt.viewmodel.GaleriaViewModel
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.lugares.utiles.AudioUtiles
import com.lugares.utiles.ImagenUtiles
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

// FragmentAddArteBinding = FragmentAddGaleriaBinding

class AddArteFragment : Fragment() {

    private lateinit var galeriaViewModel: GaleriaViewModel

    private var _binding: FragmentAddArteBinding? = null
    private val binding get() = _binding!!
    private lateinit var audioUtiles: AudioUtiles
    private lateinit var imagenUtiles: ImagenUtiles
    private lateinit var tomarFotoActivity: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        galeriaViewModel = ViewModelProvider(this)[GaleriaViewModel::class.java]
        _binding = FragmentAddArteBinding.inflate(inflater, container,false)

        // agregar una arte
        binding.btAdd.setOnClickListener{
            binding.progressBar.visibility = ProgressBar.VISIBLE
            binding.msgMensaje.text = getString(R.string.msg_subiendo_audio)
            binding.msgMensaje.visibility = TextView.VISIBLE
            subeAudio()
        }

        audioUtiles = AudioUtiles(requireActivity(),
            requireContext(),
            binding.btAccion,
            binding.btPlay,
            binding.btDelete,
            getString(R.string.msg_graba_audio),
            getString(R.string.msg_detener_audio))

        tomarFotoActivity = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()){
                result ->
            if (result.resultCode == Activity.RESULT_OK){
                imagenUtiles.actualizaFoto()
            }
        }

        imagenUtiles = ImagenUtiles(
            requireContext(),
            binding.btPhoto,
            binding.btRotaL,
            binding.btRotaR,
            binding.imagen,
            tomarFotoActivity)
        ubicaGPS()
        return binding.root
    }

    private fun ubicaGPS() {
        val ubicacion: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        if(ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ){//Sino tengo los permisos, los pido
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION),105)
        }else{ //Tengo los permisos entonces recupero las coordenadas...
            ubicacion.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if(location!=null){ //Se pudo obtener las coordenadas
                        binding.tvLongitud.text = "${location.longitude}"
                        binding.tvLatitud.text = "${location.latitude}"
                        binding.tvAltura.text = "${location.altitude}"
                    }else{ //No se logró obtener las coordenadas
                        binding.tvLongitud.text = "0.00"
                        binding.tvLatitud.text = "0.00"
                        binding.tvAltura.text = "0.00"
                    }
                }
        }
    }

    // Galeria
    private fun addArte(rutaAudio: String, rutaImagen: String) {
        val autor = binding.etAutor.text.toString()
        val correo = binding.etCorreo.text.toString()
        val telefono = binding.etTelefono.text.toString()
        val web = binding.etWeb.text.toString()
        val latitud = binding.tvLatitud.text.toString().toDouble()
        val longitud = binding.tvLongitud.text.toString().toDouble()
        val altura = binding.tvAltura.text.toString().toDouble()
        val comp = binding.cbCompartido.isChecked
        if (autor.isNotEmpty()) {
            val arte = Galeria("", autor, correo, telefono, web, latitud, altura, longitud, rutaAudio, rutaImagen)
            if(comp) {
                galeriaViewModel.saveArteG(arte)
            }
            else{
                galeriaViewModel.saveArte(arte)
            }
            Toast.makeText(requireContext(), getString(R.string.arteAdded), Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_addArteFragment_to_nav_galeria)
        } else {
            Toast.makeText(requireContext(), getString(R.string.notAdded), Toast.LENGTH_SHORT).show()
        }

    }

    private fun subeAudio() {
        if (audioUtiles.getaudioGrabado()) {
            val audioFile = audioUtiles.audioFile
            if (audioFile.exists() && audioFile.isFile && audioFile.canRead()) {
                val ruta = Uri.fromFile(audioFile)
                val rutaNube =
                    "gallartApp/${Firebase.auth.currentUser?.email}/audios/${audioFile.name}"
                val referencia: StorageReference = Firebase.storage.reference.child(rutaNube)
                //Sube el archivo a la nube
                referencia.putFile(ruta)
                    .addOnSuccessListener {
                        referencia.downloadUrl
                            .addOnSuccessListener {
                                val rutaAudio = it.toString()
                                subeImagen(rutaAudio)
                            }
                    }
                    .addOnFailureListener {
                        //Error al grabar audio en la nube
                        subeImagen("")
                    }
            } else {// Por alguna raazon no hay archivo de audio
                subeImagen("")
            }
        } else {//No se tomo la nota de audio
            subeImagen("")
        }
    }

    private fun subeImagen(rutaAudio: String) {
        binding.msgMensaje.text = getString(R.string.msg_subiendo_imagen)
        if (imagenUtiles.getFotoTomada()) {
            val imagenFile = imagenUtiles.imagenFile
            if (imagenFile.exists() && imagenFile.isFile && imagenFile.canRead()) {
                val ruta = Uri.fromFile(imagenFile)
                val rutaNube =
                    "gallartApp/${Firebase.auth.currentUser?.email}/imagenes/${imagenFile.name}"
                val referencia: StorageReference = Firebase.storage.reference.child(rutaNube)
                //Sube el archivo a la nube
                referencia.putFile(ruta)
                    .addOnSuccessListener {
                        referencia.downloadUrl
                            .addOnSuccessListener {
                                val rutaImagen = it.toString()
                                addArte(rutaAudio, rutaImagen)
                            }
                    }
                    .addOnFailureListener {
                        addArte(rutaAudio, "")
                    }
            } else {
                addArte(rutaAudio, "")
            }
        } else {
            addArte(rutaAudio, "")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}