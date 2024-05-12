package com.example.proyecto_fixit_final

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.portada)
    }

    fun OpenSelectUser(view: View) {
        val intent = Intent(this, SelectUser::class.java)
        startActivity(intent)
    }

}
