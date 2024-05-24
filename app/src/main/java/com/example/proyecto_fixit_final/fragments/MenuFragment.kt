package com.example.proyecto_fixit_final.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.proyecto_fixit_final.MainActivity
import com.example.proyecto_fixit_final.Specialist.ProfileSpecialist
import com.example.proyecto_fixit_final.Specialist.ChangePasswordSpecialist
import com.example.proyecto_fixit_final.R
import com.example.proyecto_fixit_final.Specialist.CreateServicesSpecialist
import com.example.proyecto_fixit_final.Specialist.ViewSpecialistServices
import com.google.firebase.auth.FirebaseAuth

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

        // Botones en el layout con los ids 'volver', 'rectangle1', 'rectangle2', 'rectangle3', 'rectangle4', y 'rectangle5'
        view.findViewById<View>(R.id.volver).setOnClickListener { returnHome() }
        view.findViewById<View>(R.id.rectangle1).setOnClickListener { openProfile() }
        view.findViewById<View>(R.id.rectangle4).setOnClickListener { openCredentialsSpecialist() }
        view.findViewById<View>(R.id.rectangle2).setOnClickListener { goOfferNewService() }
        view.findViewById<View>(R.id.rectangle3).setOnClickListener { goServices() }
        view.findViewById<View>(R.id.rectangle5).setOnClickListener { outSesion() }
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

    // Función para abrir la lista de ofertas de servicios
    fun goOfferNewService() {
        val intent = Intent(requireActivity(), CreateServicesSpecialist::class.java)
        startActivity(intent)
    }

    // Función para abrir la lista de servicios del especialista
    fun goServices() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            val intent = Intent(requireActivity(), ViewSpecialistServices::class.java)
            intent.putExtra("uid", uid)
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), "Error: Usuario no autenticado.", Toast.LENGTH_SHORT).show()
        }
    }

    fun outSesion() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
        Toast.makeText(requireContext(), "Sesión cerrada exitosamente", Toast.LENGTH_SHORT).show()
    }
}
