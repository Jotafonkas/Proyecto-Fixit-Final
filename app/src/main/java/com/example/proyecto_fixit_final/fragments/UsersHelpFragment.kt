package com.example.proyecto_fixit_final.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.proyecto_fixit_final.R

class UsersHelpFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_help, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Botones en el layout con los ids 'btnLlamar' y 'btnCorreo'
        view.findViewById<Button>(R.id.btnLlamar).setOnClickListener { v -> llamar(v) }
        view.findViewById<Button>(R.id.btnCorreo).setOnClickListener { v -> correoEnviar(v) }
    }

    // Función para llamar
    fun llamar(view: View) {
        // Crear un intent para iniciar la aplicación de marcado de teléfono con el número deseado
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:+56975010313")
        startActivity(intent)
    }

    // Función para enviar correo
    fun correoEnviar(view: View) {
        // Crear un intent para iniciar la aplicación de correo electrónico con la dirección deseada
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:fixitoficial2024@gmail.com")
        startActivity(intent)
    }
}