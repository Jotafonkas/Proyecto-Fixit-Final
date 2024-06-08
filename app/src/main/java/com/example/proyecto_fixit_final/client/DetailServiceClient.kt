package com.example.proyecto_fixit_final.Specialist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto_fixit_final.R
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore

class DetailServiceClient : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detalle_servicio_cliente)

        // Obtención de datos pasados por el intent
        val uid = intent.getStringExtra("uid") ?: ""
        val nombre = intent.getStringExtra("nombre") ?: ""
        val nombreServicio = intent.getStringExtra("nombreServicio") ?: ""
        // val categoria = intent.getStringExtra("categoria") ?: ""
        val imageUrl = intent.getStringExtra("imageUrl") ?: ""

        // Referencias a las vistas
        val imageView: ShapeableImageView = findViewById(R.id.imageServiceSpecialist)
        val nombreTextView: TextView = findViewById(R.id.input_nombre_especialista)
        val nombreServicioTextView: TextView = findViewById(R.id.input_nombre_servicio)
        // val categoriaTextView: TextView = findViewById(R.id.categoria_servicio)
        val descripcionTextView: TextView = findViewById(R.id.input_descripcion_servicio)

        // Asignación de datos a las vistas
        nombreTextView.text = nombre
        nombreServicioTextView.text = nombreServicio
        // categoriaTextView.text = categoria
        Picasso.get().load(imageUrl).into(imageView)

        // Obtener la descripción del servicio desde Firebase
        val db = FirebaseFirestore.getInstance()
        db.collection("especialistas")
            .document(uid)
            .collection("servicios")
            .whereEqualTo("nombreServicio", nombreServicio)
            .get()
            .addOnSuccessListener { serviceDocuments ->
                for (serviceDocument in serviceDocuments) {
                    val descripcionServicio = serviceDocument.getString("descripcionServicio") ?: ""
                    descripcionTextView.text = descripcionServicio
                }
            }
            .addOnFailureListener { e ->
                descripcionTextView.text = "Error al cargar la descripción: ${e.message}"
            }
    }
}
