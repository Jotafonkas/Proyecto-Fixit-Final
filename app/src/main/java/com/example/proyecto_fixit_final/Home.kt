package com.example.proyecto_fixit_final

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import fragments.HelpSpecialistFragment
import fragments.HomeSpecialistFragment
import fragments.MenuSpecialistFragment
import fragments.ProfileSpecialistFragment
import fragments.ServiceSpecialistFragment

class Home : AppCompatActivity() {

    private lateinit var headerTextView: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home) // Asegúrate de usar el layout correcto

        headerTextView = findViewById(R.id.header)
        auth = FirebaseAuth.getInstance()
        firestore = Firebase.firestore

        // Recibir el correo pasado desde LoginSpecialist
        val user = auth.currentUser
        if (user != null) {
            // Recuperar y mostrar el nombre del usuario desde Firestore
            fetchAndDisplayUserName(user.uid)
        }

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    replaceFragment(HomeSpecialistFragment())
                    true
                }
                R.id.services -> {
                    replaceFragment(ServiceSpecialistFragment())
                    true
                }
                R.id.menu -> {
                    replaceFragment(MenuSpecialistFragment())
                    true
                }
                R.id.help -> {
                    replaceFragment(HelpSpecialistFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    // Function to open profile
    fun openProfile() {
        replaceFragment(ProfileSpecialistFragment())
    }

    // Método para abrir el perfil
    fun openProfile(view: android.view.View) {
        val user = auth.currentUser
        if (user != null) {
            // Usuario logueado, abrir la actividad de perfil
            val intent = Intent(this, ProfileSpecialist::class.java)
            intent.putExtra("correo", user.email)
            startActivity(intent)
        } else {
            // Usuario no logueado, mostrar mensaje de error
            Toast.makeText(this, "Por favor, inicie sesión para acceder a su perfil", Toast.LENGTH_LONG).show()
        }
    }

    private fun fetchAndDisplayUserName(uid: String) {
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val nombre = document.getString("nombre")
                    headerTextView.text = "Bienvenid@, $nombre"
                } else {
                    Toast.makeText(this, "No se encontró el usuario.", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al obtener el usuario: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
