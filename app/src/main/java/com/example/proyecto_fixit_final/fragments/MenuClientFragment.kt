package com.example.proyecto_fixit_final.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.proyecto_fixit_final.MainActivity
import com.example.proyecto_fixit_final.R
import com.example.proyecto_fixit_final.Client.ProfileClient
import com.example.proyecto_fixit_final.Client.ChangePasswordClient
import com.example.proyecto_fixit_final.Client.ServicesHistory
import com.google.firebase.auth.FirebaseAuth

class MenuClientFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu_client, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Botones en el layout con los ids 'rectangle1', 'rectangle2' y 'rectangle3'
        view.findViewById<View>(R.id.rectangle1).setOnClickListener { openProfileClient() }
        view.findViewById<View>(R.id.rectangle2).setOnClickListener { openCredentialsClient() }
        view.findViewById<View>(R.id.rectangle4).setOnClickListener { mostrarDialogoConfirmacion() }
        view.findViewById<View>(R.id.rectangle3).setOnClickListener { ServiceHistory() }
    }

    // Función para abrir el perfil
    fun openProfileClient() {
        val intent = Intent(requireActivity(), ProfileClient::class.java)
        startActivity(intent)
    }

    // Función para abrir las credenciales
    fun openCredentialsClient() {
        val intent = Intent(requireActivity(), ChangePasswordClient::class.java)
        startActivity(intent)
    }
    // Función para abrir el Historial de servicios
    fun ServiceHistory() {
        val intent = Intent(requireActivity(), ServicesHistory::class.java)
        startActivity(intent)
    }

    // Función para mostrar el diálogo de confirmación antes de cerrar sesión
    private fun mostrarDialogoConfirmacion() {
        // Creamos un diálogo de alerta
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Cerrar Sesión")
        builder.setMessage("¿Está seguro que desea cerrar sesión?")

        // Botones del diálogo
        builder.setPositiveButton("Sí") { dialog, _ ->
            cerrarSesion()
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    // Función para cerrar sesión
    private fun cerrarSesion() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
        Toast.makeText(requireContext(), "Sesión cerrada exitosamente", Toast.LENGTH_SHORT).show()
    }
}