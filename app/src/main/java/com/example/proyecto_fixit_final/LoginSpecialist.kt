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
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_especialista)
        auth = FirebaseAuth.getInstance()
        Email_Login_Specialist = findViewById(R.id.Email_Login_Specialist)
        password_login_specialist = findViewById(R.id.password_login_specialist)
        btn_login_specialist = findViewById(R.id.btn_login_specialist)

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

    private fun iniciarSesion(correo: String, pass: String) {
        auth.signInWithEmailAndPassword(correo, pass)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Conectado correctamente", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, Home::class.java)
                    intent.putExtra("correo", correo) // Pasar el correo a la actividad Home
                    startActivity(intent)
                    finish() // Opcional: Llama a finish() si deseas cerrar la actividad de login
                } else {
                    Toast.makeText(this, "ERROR", Toast.LENGTH_LONG).show()
                }
            }
    }
}
