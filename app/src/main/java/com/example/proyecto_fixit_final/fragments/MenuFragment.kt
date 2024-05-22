package com.example.proyecto_fixit_final.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.proyecto_fixit_final.Specialist.ProfileSpecialist
import com.example.proyecto_fixit_final.Specialist.ChangePasswordSpecialist
import com.example.proyecto_fixit_final.R

class MenuFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Botones en el layout con los ids 'volver', 'rectangle1' y 'rectangle4'
        view.findViewById<View>(R.id.volver).setOnClickListener { v -> returnHome() }
        view.findViewById<View>(R.id.rectangle1).setOnClickListener { v -> openProfile() }
        view.findViewById<View>(R.id.rectangle4).setOnClickListener { v -> openCredentialsSpecialist() }
    }

    // Función para volver a la pantalla principal
    fun returnHome() {
        val intent = Intent(requireActivity(), HomeFragment::class.java)
        startActivity(intent)
    }

    // Función para abrir el perfil
    fun openProfile() {
        val intent = Intent(requireActivity(), ProfileSpecialist::class.java)
        startActivity(intent)
    }

    // Función para abrir las credenciales
    fun openCredentialsSpecialist() {
        val intent = Intent(requireActivity(), ChangePasswordSpecialist::class.java)
        startActivity(intent)
    }
}