package com.example.proyecto_fixit_final

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MenuSpecialist : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_especialista)
    }

    fun returnHome(view: View) {
        val intent = Intent(this, Home::class.java)
        startActivity(intent)
    }

    fun openProfile(view: View) {
        val intent = Intent(this, ProfileSpecialist::class.java)
        startActivity(intent)
    }

}

