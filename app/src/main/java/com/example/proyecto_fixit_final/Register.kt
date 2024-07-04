package com.example.proyecto_fixit_final

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.proyecto_fixit_final.Client.ClientRegister
import com.example.proyecto_fixit_final.Specialist.SpecialistRegister

class Register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_registro)
    }

    // Funcion para abrir la actividad de registro de especialista
    fun openRegisterSpecialist(view: View) {
        val intent = Intent(this, SpecialistRegister::class.java)
        startActivity(intent)
    }
    fun openRegisterClient(view: View) {
        val intent = Intent(this, ClientRegister::class.java)
        startActivity(intent)
    }
}