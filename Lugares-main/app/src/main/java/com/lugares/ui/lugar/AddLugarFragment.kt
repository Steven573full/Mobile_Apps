package com.lugares.ui.lugar

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
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.lugares.R
import com.lugares.databinding.FragmentAddLugarBinding
import com.lugares.model.Lugar
import com.lugares.utiles.AudioUtiles
import com.lugares.utiles.ImagenUtiles
import com.lugares.viewmodel.LugarViewModel

class AddLugarFragment : Fragment() {

    //Variable para acceder a la parte de datos BD
    private lateinit var lugarViewModel: LugarViewModel

    //Accedemos a la parte visual de addLugar
    private var _binding: FragmentAddLugarBinding? = null

    private lateinit var audioUtiles: AudioUtiles

    private lateinit var imagenUtiles: ImagenUtiles
    private lateinit var tomarFotoActivity: ActivityResultLauncher<Intent>

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        lugarViewModel = ViewModelProvider(this)[LugarViewModel::class.java]

        _binding = FragmentAddLugarBinding.inflate(inflater, container, false)

        //Darle función al botor AGREGAR del fragment_add_lugar.xml
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

    private fun ubicaGPS(){
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
                        binding.tvAltura.text = "$0.00"
                    }
                }
        }
    }

    private fun subeAudio() {
        if (audioUtiles.getaudioGrabado()) {
            val audioFile = audioUtiles.audioFile
            if (audioFile.exists() && audioFile.isFile && audioFile.canRead()) {
                val ruta = Uri.fromFile(audioFile)
                val rutaNube =
                    "lugaresApp/${Firebase.auth.currentUser?.email}/audios/${audioFile.name}"
                val referencia: StorageReference = Firebase.storage.reference.child(rutaNube)
                referencia.putFile(ruta)
                    .addOnSuccessListener {
                        referencia.downloadUrl
                            .addOnSuccessListener {
                                val rutaAudio = it.toString()
                                subeImagen(rutaAudio)
                            }
                    }
                    .addOnFailureListener {
                        //Error al grabar audio en la nube...
                        subeImagen("")
                    }
            } else { //Por alguna razón no hay archivo de audio...
                subeImagen("")
            }
        } else {  //No se tomó la nota de audio
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
                    "lugaresApp/${Firebase.auth.currentUser?.email}/imagenes/${imagenFile.name}"
                val referencia: StorageReference = Firebase.storage.reference.child(rutaNube)
                referencia.putFile(ruta)
                    .addOnSuccessListener {
                        referencia.downloadUrl
                            .addOnSuccessListener {
                                val rutaImagen = it.toString()
                                addLugar(rutaAudio, rutaImagen)
                            }
                    }
                    .addOnFailureListener {
                        addLugar(rutaAudio, "")
                    }
            } else {
                addLugar(rutaAudio, "")
            }
        } else {
            addLugar(rutaAudio, "")
        }

    }

    //Función para agregar un lugar
    private fun addLugar(rutaAudio: String, rutaImagen: String) {
        //Recuperamos la info agregado
        val nombre = binding.etNombre.text.toString()
        val correo = binding.etCorreo.text.toString()
        val telefono = binding.etTelefono.text.toString()
        val web = binding.etWeb.text.toString()
        val latitud = binding.tvLatitud.text.toString().toDouble()
        val longitud = binding.tvLongitud.text.toString().toDouble()
        val altura = binding.tvAltura.text.toString().toDouble()

        //If para saber si el nombre está vacio
        if(nombre.isNotEmpty()){
            //Creamos un lugar con la ruta que crea la persona
            val lugar = Lugar("",nombre,correo,telefono,web, latitud, longitud, altura, rutaAudio,rutaImagen)
            //Agregamos un lugar a la BD
            lugarViewModel.saveLugar(lugar)
            //Mensaje emergente, agregamos mensaje al lugarAdded
            Toast.makeText(requireContext(), getString(R.string.lugarAdded), Toast.LENGTH_SHORT).show()
            //Nos devolvemos a la pantalla Lugar
            findNavController().navigate(R.id.action_addLugarFragment_to_nav_lugar)
        }else{
            //Error por si dejamos en blanco un espacio
            Toast.makeText(requireContext(), getString(R.string.noData), Toast.LENGTH_SHORT).show()
        }
    }

    //Cuando el fragmento se deja de ver, se libera la memoria
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}