package com.example.proyecto_fixit_final

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class Home: AppCompatActivity() {

    private lateinit var headerTextView: TextView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home) // Asegúrate de usar el layout correcto

        headerTextView = findViewById(R.id.header)
        auth = FirebaseAuth.getInstance()

        // Recibir el correo pasado desde LoginSpecialist
        val correo = intent.getStringExtra("correo")

        // Actualizar el TextView con el mensaje de bienvenida
        headerTextView.text = "Bienvenid@, $correo"
    }

    // Método para abrir el perfil
    fun openMenu(view: android.view.View) {
        val user = auth.currentUser
        if (user != null) {
            // Usuario logueado, abrir la actividad de perfil
            val intent = Intent(this, MenuSpecialist::class.java)
            intent.putExtra("correo", user.email)
            startActivity(intent)
        } else {
            // Usuario no logueado, mostrar mensaje de error
            Toast.makeText(this, "Por favor, inicie sesión para acceder a su perfil", Toast.LENGTH_LONG).show()
        }
    }
}
