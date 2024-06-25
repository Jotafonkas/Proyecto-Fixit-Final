package com.example.proyecto_fixit_final.Client

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto_fixit_final.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class PersonalProfileSpecialist : AppCompatActivity() {

    private var telefono: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.perfil_personal_especialista)

        // Obtén el uid del intent
        val uid = intent.getStringExtra("uid") ?: ""

        if (uid.isEmpty()) {
            // Manejar el caso en que el uid esté vacío o no se haya pasado correctamente
            finish()
            return
        }

        // Referencias a las vistas
        val imageView: ShapeableImageView = findViewById(R.id.imageServiceSpecialist)
        val nombreTextView: TextView = findViewById(R.id.input_nombre_especialista)
        val profesionTextView: TextView = findViewById(R.id.input_nombre_servicio)
        val correoTextView: TextView = findViewById(R.id.input_correo)
        val ciudadTextView: TextView = findViewById(R.id.input_ciudad)
        val botonContactar: Button = findViewById(R.id.botoncontactar)

        // Obtener los datos del especialista desde Firebase
        val db = FirebaseFirestore.getInstance()
        db.collection("especialistas")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val nombre = document.getString("nombre") ?: ""
                    val profesion = document.getString("profesion") ?: ""
                    val correo = document.getString("correo") ?: ""
                    val imageUrl = document.getString("imageUrl") ?: ""
                    val ciudad = document.getString("ciudad") ?: ""
                    telefono = document.getString("telefono") ?: ""

                    // Asignar los datos a las vistas
                    nombreTextView.text = nombre
                    profesionTextView.text = profesion
                    correoTextView.text = correo
                    ciudadTextView.text = ciudad
                    if (imageUrl.isNotEmpty()) {
                        Picasso.get().load(imageUrl).into(imageView)
                    }else{
                        imageView.setImageResource(R.drawable.imagen_login) // Imagen predeterminada
                    }
                } else {
                    // Manejar el caso en que el documento no existe
                }
            }
            .addOnFailureListener { e ->
                // Manejar errores
            }

        // Asignar el listener al botón
        botonContactar.setOnClickListener { goPhone(it) }
    }

    // Función para llamar
    fun goPhone(view: View) {
        telefono?.let {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$it")
            startActivity(intent)
        } ?: run {
            // Manejar el caso en que el teléfono sea nulo
        }
    }

    fun backMenu(view: View) {
        onBackPressed()
    }
}
