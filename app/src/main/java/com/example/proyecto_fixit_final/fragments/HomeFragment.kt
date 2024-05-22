package com.example.proyecto_fixit_final.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.proyecto_fixit_final.Specialist.ProfileSpecialist
import com.example.proyecto_fixit_final.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private lateinit var headerTextView: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Asignación de la variable 'headerTextView' al TextView con id 'header'
        headerTextView = view.findViewById(R.id.header)
        auth = FirebaseAuth.getInstance()
        firestore = Firebase.firestore
        val buttonOpenProfile: Button = view.findViewById(R.id.btnOpenProfile)
        val user = auth.currentUser
        if (user != null) {
            fetchAndDisplayUserName(user.uid)
        }

        buttonOpenProfile.setOnClickListener {
            openProfile()
        }

        return view
    }

    // Función para abrir el perfil
    private fun openProfile() {
        val intent = Intent(requireActivity(), ProfileSpecialist::class.java)
        startActivity(intent)
    }

    // Función para obtener y mostrar el nombre del usuario
    private fun fetchAndDisplayUserName(uid: String) {
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val nombre = document.getString("nombre")
                    headerTextView.text = "Bienvenid@, $nombre"
                }
            }
    }
}