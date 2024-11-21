package com.lugares.ui.lugar

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
import com.lugares.R
import com.lugares.databinding.FragmentUpdateLugarBinding
import com.lugares.model.Lugar
import com.lugares.viewmodel.LugarViewModel

class UpdateLugarFragment : Fragment() {

    //Variable para acceder a la parte de datos BD
    private lateinit var lugarViewModel: LugarViewModel

    //Accedemos a la parte visual de addLugar
    private var _binding: FragmentUpdateLugarBinding? = null

    private val binding get() = _binding!!

    //Definimos un argumento
    private val args by navArgs<UpdateLugarFragmentArgs>()

    //Para escuchar el audio grabado...
    private lateinit var mediaPlayer: MediaPlayer


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        lugarViewModel = ViewModelProvider(this)[LugarViewModel::class.java]

        _binding = FragmentUpdateLugarBinding.inflate(inflater, container, false)

        //Se coloca la info del objeto del lugar que me pasaron
        binding.etNombre.setText(args.lugar.nombre)
        binding.etTelefono.setText(args.lugar.telefono)
        binding.etCorreo.setText(args.lugar.correo)
        binding.etWeb.setText(args.lugar.web)

        binding.tvAltura.text=args.lugar.altura.toString()
        binding.tvLatitud.text=args.lugar.latitud.toString()
        binding.tvLongitud.text=args.lugar.longitud.toString()

        //Darle función al botor ACTUALIZAR del fragment_update_lugar.xml
        binding.btActualizar.setOnClickListener{updateLugar()}

        binding.btEmail.setOnClickListener { escribirCorreo() }
        binding.btPhone.setOnClickListener { llamarLugar() }
        //binding.btWhatsapp.setOnClickListener { enviarWhatsapp() }
        binding.btWeb.setOnClickListener { verWeb() }

        if(args.lugar.rutaAudio?.isNotEmpty()==true){
            mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(args.lugar.rutaAudio)
            mediaPlayer.prepare()
            binding.btPlay.isEnabled=true
            binding.btPlay.setOnClickListener { mediaPlayer.start() }
        }else{
            binding.btPlay.isEnabled=false
        }

        if(args.lugar.rutaImagen?.isNotEmpty()==true){
            Glide.with(requireContext())
                .load(args.lugar.rutaImagen)
                .fitCenter()
                .into(binding.imagen)
        }
        binding.btLocation.setOnClickListener { verMapa() }

        binding.btWhatsapp.setOnClickListener { enviarWhatsapp() }
        //Pregunta si tiene opciones de menú y le decimos que sí
        setHasOptionsMenu(true)

        return binding.root
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

    private fun llamarLugar() {
        //Se recupera el numero de telefono del lugar
        val recurso = binding.etTelefono.text.toString()
        if(recurso.isNotEmpty()){
            //Se activa el correo
            val rutina = Intent(Intent.ACTION_CALL)
            rutina.data = Uri.parse("tel: $recurso")
            //Para el import de Manifest importamos el de Android y no el de Java
            if(requireActivity().checkSelfPermission(Manifest.permission.CALL_PHONE)
            != PackageManager.PERMISSION_GRANTED){
                //Se solicitan los permisos.. porque no están otorgados
                requireActivity()
                    .requestPermissions(arrayOf(Manifest.permission.CALL_PHONE),105)
            }else{
                //Se tienen los permisos para llamar
                requireActivity().startActivity(rutina)//Hace la llamada
            }

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
        if(recurso.isNotEmpty()){
            //Se activa el correo
            val rutina = Intent(Intent.ACTION_SEND)
            rutina.type="message/rfc822"
            rutina.putExtra(Intent.EXTRA_EMAIL, arrayOf(recurso))
            rutina.putExtra(Intent.EXTRA_SUBJECT,
                getString(R.string.msg_saludos)+" "+binding.etNombre.text)
            rutina.putExtra(Intent.EXTRA_TEXT,
            getString(R.string.msg_mensaje_correo))
            startActivity(rutina) //Levanta el correo y lo presenta para modificar y enviar
        }else{
            Toast.makeText(
                requireContext(),
                getString(R.string.msg_datos), Toast.LENGTH_SHORT
            ).show()
        }
    }

    //acá se genera el menú con el icono de borrar
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.delete_menu, menu)
    }

    //Acá se programa que si se detecta un click en el icono Borrar... haga algo
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //Pregunto si se dió click en el icono de borrado
        if(item.itemId==R.id.menu_delete){
            //Hace algo si se dio click
            //Llamamos la función...
            deleteLugar()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteLugar() {
        val consulta= AlertDialog.Builder(requireContext())
        //Consultamos si quiere eliminar el lugar
        consulta.setTitle(R.string.delete)
        //Ponemos un mensaje a la ventana
        consulta.setMessage(getString(R.string.seguroBorrar)+" ${args.lugar.nombre}?")

        //Acciones a ejecutar si respondo YES
        consulta.setPositiveButton(getString(R.string.si)){_,_ ->
            //Borramos el lugar... sin consultar...
            lugarViewModel.deleteLugar(args.lugar)
            findNavController().navigate(R.id.action_updateLugarFragment_to_nav_lugar)
        }

        //Si la respuesta es no
        consulta.setNegativeButton(getString(R.string.no)){_,_ -> }

        //Crear la alerta y mostrarla
        consulta.create().show()
    }

    //Función para actualizar un lugar
    private fun updateLugar() {
        //Recuperamos la info agregado
        val nombre = binding.etNombre.text.toString()
        val correo = binding.etCorreo.text.toString()
        val telefono = binding.etTelefono.text.toString()
        val web = binding.etWeb.text.toString()
        //If para saber si el nombre está vacio
        if(nombre.isNotEmpty()){
            //Creamos un lugar con la ruta que crea la persona
            val lugar = Lugar(args.lugar.id,nombre,correo,telefono,web, 0.0, 0.0, 0.0, "","")
            //Agregamos un lugar a la BD
            lugarViewModel.saveLugar(lugar)
            //Mensaje emergente, agregamos mensaje al lugarAdded
            Toast.makeText(requireContext(), getString(R.string.lugarAdded), Toast.LENGTH_SHORT).show()
            //Nos devolvemos a la pantalla Lugar
            findNavController().navigate(R.id.action_updateLugarFragment_to_nav_lugar)
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