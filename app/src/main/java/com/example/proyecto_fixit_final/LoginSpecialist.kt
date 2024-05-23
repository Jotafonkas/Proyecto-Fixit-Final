package com.example.proyecto_fixit_final

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class LoginSpecialist : AppCompatActivity() {

    private lateinit var Email_Login_Specialist: TextInputEditText
    private lateinit var password_login_specialist: TextInputEditText
    private lateinit var btn_login_specialist: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_especialista)
        // Inicializar Firebase Auth
        auth = FirebaseAuth.getInstance()
        Email_Login_Specialist = findViewById(R.id.Email_Login_Specialist) // Obtener el correo
        password_login_specialist = findViewById(R.id.password_login_specialist) // Obtener la contraseña
        btn_login_specialist = findViewById(R.id.btn_login_specialist) // Obtener el botón

        // login en click al botón
        btn_login_specialist.setOnClickListener {
            // recuperar datos
            val correo = Email_Login_Specialist.text.toString()
            val pass = password_login_specialist.text.toString()

            if (correo.isEmpty()) {
                Email_Login_Specialist.error = "Ingrese un correo"
                return@setOnClickListener
            }
            if (pass.isEmpty()) {
                password_login_specialist.error = "Ingrese su contraseña"
                return@setOnClickListener
            }
            iniciarSesion(correo, pass)
        }
    }

    // Iniciar sesión
    private fun iniciarSesion(correo: String, pass: String) {
        auth.signInWithEmailAndPassword(correo, pass)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Conectado correctamente", Toast.LENGTH_LONG).show()
                    val user = auth.currentUser
                    val intent = Intent(this, NavBar::class.java)
                    intent.putExtra("uid", user?.uid) // Pasar el UID del usuario a la actividad Home
                    startActivity(intent)
                    finish() // Opcional: Llama a finish() si deseas cerrar la actividad de login
                } else {
                    Toast.makeText(this, "ERROR", Toast.LENGTH_LONG).show()
                }
            }
    }
}
