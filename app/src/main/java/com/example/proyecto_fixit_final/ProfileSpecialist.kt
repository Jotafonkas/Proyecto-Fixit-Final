package com.example.proyecto_fixit_final

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class ProfileSpecialist : AppCompatActivity() {

    private lateinit var edCorreo: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.perfil_especialista)

        edCorreo = findViewById(R.id.edCorreo)

        // Recibir el correo pasado desde HomeActivity
        val correo = intent.getStringExtra("correo")

        // Poner el correo en el campo de EditText
        if (correo != null) {
            edCorreo.setText(correo)
        }
    }
}
