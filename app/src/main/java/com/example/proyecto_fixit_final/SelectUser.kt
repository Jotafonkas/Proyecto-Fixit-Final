package com.example.proyecto_fixit_final

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.proyecto_fixit_final.Client.RegisterClient
import com.example.proyecto_fixit_final.Specialist.RegisterSpecialist

class SelectUser : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.seleccion_usuario)
    }

    // Funcion para abrir la actividad de registro de especialista
    fun openRegisterSpecialist(view: View) {
        val intent = Intent(this, RegisterSpecialist::class.java)
        startActivity(intent)
    }
    fun openRegisterClient(view: View) {
        val intent = Intent(this, RegisterClient::class.java)
        startActivity(intent)
    }
}