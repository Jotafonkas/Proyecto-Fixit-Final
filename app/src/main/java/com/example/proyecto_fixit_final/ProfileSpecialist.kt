package com.example.proyecto_fixit_final

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileSpecialist : AppCompatActivity() {

    private lateinit var edCorreo: EditText
    private lateinit var edRut: EditText
    private lateinit var edNombre: EditText
    private lateinit var edProfesion: EditText
    private lateinit var edTelefono: EditText
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.perfil_especialista)
        auth = FirebaseAuth.getInstance()
        firestore = Firebase.firestore
        edCorreo = findViewById(R.id.edCorreo)
        edRut = findViewById(R.id.edRut)
        edNombre = findViewById(R.id.edNombre)
        edProfesion = findViewById(R.id.edEspecialidad)
        edTelefono = findViewById(R.id.edTelefono)
        edCorreo = findViewById(R.id.edCorreo)

        // Recibir el correo pasado desde HomeActivity
        val correo = intent.getStringExtra("correo")

        // Poner el correo en el campo de EditText
        val user = auth.currentUser
        if (user != null) {
            // Recuperar y mostrar el nombre del usuario desde Firestore
            fetchAndDisplayData(user.uid)
        }
    }

    private fun fetchAndDisplayData(uid: String) {
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val nombre = document.getString("nombre")
                    val correo = document.getString("correo")
                    val rut = document.getString("rut")
                    val profesion = document.getString("profesion")
                    val telefono = document.getString("telefono")
                    edNombre.setText(nombre)
                    edCorreo.setText(correo)
                    edRut.setText(rut)
                    edProfesion.setText(profesion)
                    edTelefono.setText(telefono)
                } else {
                    Toast.makeText(this, "No se encontró el usuario.", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al obtener el usuario: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

}
