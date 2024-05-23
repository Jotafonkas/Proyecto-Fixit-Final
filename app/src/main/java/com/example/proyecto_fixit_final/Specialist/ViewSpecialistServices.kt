package com.example.proyecto_fixit_final.Specialist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto_fixit_final.R
import com.example.proyecto_fixit_final.fragments.HelpFragment
import com.example.proyecto_fixit_final.fragments.MenuFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

// Clase que muestra los servicios del especialista
class ViewSpecialistServices : AppCompatActivity() {
    private lateinit var serviciosContainer: LinearLayout
    private lateinit var uid: String

    // Función que se ejecuta al crear la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ver_servicios_especialista)

        // Obtener el UID del usuario logeado
        uid = intent.getStringExtra("uid") ?: ""

        // Se obtiene el contenedor de servicios
        serviciosContainer = findViewById(R.id.servicios_container)
        // Se cargan los servicios
        cargarServicios()
    }

    // Función que se ejecuta al presionar el botón de regresar
    fun backMenu(view: View) {
        val intent = Intent(this, MenuFragment::class.java)
        startActivity(intent)
    }

    // Función que se ejecuta al presionar el botón de ayuda
    fun goHelp(view: View) {
        val intent = Intent(this, HelpFragment::class.java)
        startActivity(intent)
    }

    // Función para cargar los servicios del usuario desde Firestore
    private fun cargarServicios() {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(uid).collection("servicios")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val nombreServicio = document.getString("nombreServicio") ?: ""
                    val descripcionServicio = document.getString("descripcionServicio") ?: ""
                    val precio = document.getString("precio") ?: ""
                    val imagenUrl = document.getString("imagenUrl") ?: ""

                    // Se agrega el servicio
                    agregarServicio(nombreServicio, descripcionServicio, precio, imagenUrl)
                }
            }
            .addOnFailureListener { exception ->
                // Manejar el error
                Toast.makeText(this, "Error al cargar servicios: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Función que agrega un servicio al contenedor
    private fun agregarServicio(nombre: String, descripcion: String, precio: String, imagenUrl: String) {
        val servicioView = LayoutInflater.from(this).inflate(R.layout.item_servicio, serviciosContainer, false)
        val imagenServicio = servicioView.findViewById<ImageView>(R.id.imagen_servicio)
        val txtNombreServicio = servicioView.findViewById<TextView>(R.id.edNombreServicio)
        val txtDescripcionServicio = servicioView.findViewById<TextView>(R.id.edDescripcionServicio)
        val txtPrecioServicio = servicioView.findViewById<TextView>(R.id.edPrecio)

        txtNombreServicio.text = nombre
        txtDescripcionServicio.text = descripcion
        txtPrecioServicio.text = precio

        // Se carga la imagen del servicio
        if (imagenUrl.isNotEmpty()) {
            Picasso.get().load(imagenUrl).into(imagenServicio)
        }

        // Se agrega el servicio al contenedor
        serviciosContainer.addView(servicioView)
    }
}
