package com.example.proyecto_fixit_final.Specialist

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto_fixit_final.R
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso
import android.widget.TextView
import com.example.proyecto_fixit_final.Client.ClientsComments
import com.example.proyecto_fixit_final.Client.PersonalProfileSpecialist
import com.google.firebase.firestore.FirebaseFirestore

class DetailServiceClient : AppCompatActivity() {
    private lateinit var especialistaId: String
    private lateinit var servicioId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detalle_servicio_cliente)

        // Obtención de datos pasados por el intent
        especialistaId = intent.getStringExtra("uid") ?: ""
        val nombre = intent.getStringExtra("nombre") ?: ""
        val nombreServicio = intent.getStringExtra("nombreServicio") ?: ""


        // Referencias a las vistas
        val imageView: ShapeableImageView = findViewById(R.id.imageServiceSpecialist)
        val nombreTextView: TextView = findViewById(R.id.input_nombre_especialista)
        val nombreServicioTextView: TextView = findViewById(R.id.input_nombre_servicio)
        val descripcionTextView: TextView = findViewById(R.id.input_descripcion_servicio)

        // Asignación de datos a las vistas
        nombreTextView.text = nombre
        nombreServicioTextView.text = nombreServicio

        // Obtener la descripción y la imagen del servicio desde Firebase
        val db = FirebaseFirestore.getInstance()
        db.collection("especialistas")
            .document(especialistaId)
            .collection("servicios")
            .whereEqualTo("nombreServicio", nombreServicio)
            .get()
            .addOnSuccessListener { serviceDocuments ->
                for (serviceDocument in serviceDocuments) {
                    val descripcionServicio = serviceDocument.getString("descripcionServicio") ?: ""
                    descripcionTextView.text = descripcionServicio
                    val imagenUrl = serviceDocument.getString("imagenUrl") ?: ""
                    Picasso.get().load(imagenUrl).into(imageView)
                    servicioId = serviceDocument.id // Guardar el ID del servicio
                }
            }
            .addOnFailureListener { e ->
                descripcionTextView.text = "Error al cargar la descripción: ${e.message}"
            }
    }

    fun goToProfileSpecialist(view: View) {
        val intent = Intent(this, PersonalProfileSpecialist::class.java)
        intent.putExtra("uid", especialistaId) // Pasa el uid del especialista
        startActivity(intent)
    }

    fun goToComments(view: View) {
        val intent = Intent(this, ClientsComments::class.java)
        intent.putExtra("especialistaId", especialistaId)
        intent.putExtra("servicioId", servicioId)
        startActivity(intent)
    }
    fun backServices(view: View) {
        onBackPressed()
    }
}