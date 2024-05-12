package com.example.proyecto_fixit_final

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class RegisterSpecialist : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registro_especialista)
    }

    fun openHome(view: View) {
        val intent = Intent(this, Home::class.java)
        startActivity(intent)
    }
}