package com.galartt.ui.galeria

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.galartt.R
import com.galartt.databinding.FragmentUpdateArteBinding
import com.galartt.model.Galeria
import com.galartt.viewmodel.GaleriaViewModel

// FragmentUpdateArteBinding = FragmentUpdateGaleriaBinding

class UpdateArteFragment : Fragment() {

    private val args by navArgs<UpdateArteFragmentArgs>()

    private lateinit var galeriaViewModel: GaleriaViewModel

    private var _binding: FragmentUpdateArteBinding? = null
    private val binding get() = _binding!!
    //Para escuchar el audio grabado...
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        galeriaViewModel = ViewModelProvider(this)[GaleriaViewModel::class.java]
        _binding = FragmentUpdateArteBinding.inflate(inflater, container, false)

        // se coloca la info de objeto arte que se pasa
        binding.etAutor.setText(args.arte.autor)
        binding.etTelefono.setText(args.arte.telefono)
        binding.etCorreo.setText(args.arte.correo)
        binding.etWeb.setText(args.arte.web)

        binding.tvAltura.text = args.arte.altura.toString()
        binding.tvLatitud.text = args.arte.latitud.toString()
        binding.tvLongitud.text = args.arte.longitud.toString()

        // actualizar una obra
        binding.btActualizar.setOnClickListener { updateArte() }

        binding.btEmail.setOnClickListener { escribirCorreo() }
        binding.btPhone.setOnClickListener { llamarLugar() }
        binding.btWeb.setOnClickListener { verWeb() }
        binding.btWhatsapp.setOnClickListener { enviarWhatsapp() }
        binding.btLocation.setOnClickListener { verMapa() }

        if(args.arte.rutaAudio?.isNotEmpty()==true){
            mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(args.arte.rutaAudio)
            mediaPlayer.prepare()
            binding.btPlay.isEnabled=true
            binding.btPlay.setOnClickListener { mediaPlayer.start() }
        }else{
            binding.btPlay.isEnabled=false
        }

        if(args.arte.rutaImagen?.isNotEmpty()==true){
            Glide.with(requireContext())
                .load(args.arte.rutaImagen)
                .fitCenter()
                .into(binding.imagen)
        }

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.delete_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
    //Pregunto si se dió click en el icono de borrado
        if (item.itemId == R.id.menu_delete) {
            //Hace algo si se dio click
            //Llamamos la función...
            deleteArte()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteArte() {
        val consulta = AlertDialog.Builder(requireContext())
        //Consultamos si quiere eliminar el lugar
        consulta.setTitle(R.string.delete)
        //Ponemos un mensaje a la ventana
        consulta.setMessage(getString(R.string.seguroBorrar) + " ${args.arte.autor}?")

        //Acciones a ejecutar si respondo YES
        consulta.setPositiveButton(getString(R.string.si)) { _, _ ->
            //Borramos el arte... sin consultar...
            galeriaViewModel.deleteArte(args.arte)
            findNavController().navigate(R.id.action_updateArteFragment_to_nav_galeria)
        }

        //Si la respuesta es no
        consulta.setNegativeButton(getString(R.string.no)) { _, _ -> }

        //Crear la alerta y mostrarla
        consulta.create().show()
    }

    private fun updateArte() {
        val autor = binding.etAutor.text.toString()
        val correo = binding.etCorreo.text.toString()
        val telefono = binding.etTelefono.text.toString()
        val web = binding.etWeb.text.toString()
        if (autor.isNotEmpty()) {
            val arte = Galeria(args.arte.id, autor, correo, telefono, web, 0.0, 0.0, 0.0, "", "")
            galeriaViewModel.saveArte(arte)
            Toast.makeText(requireContext(), getString(R.string.arteAdded), Toast.LENGTH_SHORT)
                .show()
            findNavController().navigate(R.id.action_updateArteFragment_to_nav_galeria)
        } else {
            Toast.makeText(requireContext(), getString(R.string.notAdded), Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun verMapa() {
        val latitud = binding.tvLatitud.text.toString().toDouble()
        val longitud = binding.tvLongitud.text.toString().toDouble()

        if(latitud.isFinite() && longitud.isFinite()){
            val location = Uri.parse("geo:$latitud, $longitud?z18")
            val mapIntent = Intent(Intent.ACTION_VIEW, location)
            startActivity(mapIntent)
        }else{
            Toast.makeText(
                requireContext(),
                getString(R.string.msg_datos), Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun escribirCorreo() {
        //Se recupera el correo del lugar
        val recurso = binding.etCorreo.text.toString()
        if (recurso.isNotEmpty()) {
            //Se activa el correo
            val rutina = Intent(Intent.ACTION_SEND)
            rutina.type = "message/rfc822"
            rutina.putExtra(Intent.EXTRA_EMAIL, arrayOf(recurso))
            rutina.putExtra(
                Intent.EXTRA_SUBJECT,
                getString(R.string.msg_saludos) + " " + binding.etAutor.text
            )
            rutina.putExtra(
                Intent.EXTRA_TEXT,
                getString(R.string.msg_mensaje_correo)
            )
            startActivity(rutina) //Levanta el correo y lo presenta para modificar y enviar
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.msg_datos), Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun llamarLugar() {
        //Se recupera el numero de telefono del lugar
        val recurso = binding.etTelefono.text.toString()
        if (recurso.isNotEmpty()) {
            //Se activa el correo
            val rutina = Intent(Intent.ACTION_CALL)
            rutina.data = Uri.parse("tel: $recurso")
            //Para el import de Manifest importamos el de Android y no el de Java
            if (requireActivity().checkSelfPermission(Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                //Se solicitan los permisos.. porque no están otorgados
                requireActivity()
                    .requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), 105)
            } else {
                //Se tienen los permisos para llamar
                requireActivity().startActivity(rutina)//Hace la llamada
            }

        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.msg_datos), Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun verWeb() {
        //Se recupera el sitio web del lugar
        val recurso = binding.etWeb.text.toString()
        if(recurso.isNotEmpty()){
            //Se abre el sitio web
            val rutina = Intent(Intent.ACTION_VIEW, Uri.parse("http://$recurso"))
            startActivity(rutina) //Levanta el browser y se ve el sitio web
        }else{
            Toast.makeText(
                requireContext(),
                getString(R.string.msg_datos), Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun enviarWhatsapp() {
        val recurso = binding.etTelefono.text.toString()
        if(recurso.isNotEmpty()){
            val sendIntent = Intent(Intent.ACTION_VIEW)
            val uri = "whatsapp://send?phone=506$recurso&text="+getString(R.string.msg_saludos)
            sendIntent.setPackage("com.whatsapp")
            sendIntent.data=Uri.parse(uri)
            startActivity(sendIntent)
        }else{
            Toast.makeText(
                requireContext(),
                getString(R.string.msg_datos), Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}