package com.example.proyecto_fixit_final.Admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto_fixit_final.R
import com.example.proyecto_fixit_final.Specialist.ViewSpecialistDetailService
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class ViewSpecialistServicesAdmin : AppCompatActivity() {

    private lateinit var serviciosContainer: LinearLayout
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ver_servicios_especialista_admin)

        serviciosContainer = findViewById(R.id.servicios_container_admin)
        firestore = FirebaseFirestore.getInstance()

        // Obtener el ID del especialista desde el Intent
        val specialistId = intent.getStringExtra("specialistId")
        if (!specialistId.isNullOrEmpty()) {
            // Buscar servicios del especialista desde Firestore
            cargarServicios(specialistId)
        } else {
            Toast.makeText(this, "No se pudo obtener el ID del especialista", Toast.LENGTH_SHORT).show()
        }

        // Configurar botones para volver al perfil del especialista y al menú
        val flechaVolver: ImageView = findViewById(R.id.flechavolver_perfilespecialista)
        val menu: TextView = findViewById(R.id.menu)

        flechaVolver.setOnClickListener { irAPerfilEspecialista() }
        menu.setOnClickListener { irAlMenu() }
    }

    // Función para cargar los servicios del especialista
    private fun cargarServicios(specialistId: String) {
        // Obtenemos la colección de servicios del especialista
        val db = FirebaseFirestore.getInstance()
        // Obtenemos los servicios del especialista
        db.collection("especialistas").document(specialistId).collection("servicios")
            .get()
            // Si se obtienen los servicios exitosamente se agregan al contenedor
            .addOnSuccessListener { result ->
                for (document in result) {
                    val nombreServicio = document.getString("nombreServicio") ?: ""
                    val descripcionServicio = document.getString("descripcionServicio") ?: ""
                    val precio = document.getString("precio") ?: ""
                    val imagenUrl = document.getString("imagenUrl") ?: ""
                    val categoriaServicio = document.getString("categoriaServicio") ?: ""
                    val documentId = document.id

                    agregarServicio(nombreServicio, descripcionServicio, precio, imagenUrl, categoriaServicio, documentId)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al cargar servicios: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Función que agrega un servicio al contenedor
    private fun agregarServicio(nombre: String, descripcion: String, precio: String, imagenUrl: String, categoria: String, documentId: String) {
        val servicioView = LayoutInflater.from(this).inflate(R.layout.card_servicios_especialista_admin, serviciosContainer, false)
        val imagenServicio = servicioView.findViewById<ImageView>(R.id.imagen_servicio)
        val txtNombreServicio = servicioView.findViewById<TextView>(R.id.edNombreServicio)
        val txtDescripcionServicio = servicioView.findViewById<TextView>(R.id.edDescripcionServicio)
        val txtPrecioServicio = servicioView.findViewById<TextView>(R.id.edPrecio)

        // Establecemos valores del servicio en la vista
        txtNombreServicio.text = nombre
        txtDescripcionServicio.text = descripcion
        txtPrecioServicio.text = precio

        // Agregar clic listener para abrir la actividad de detalle con los datos del servicio
        servicioView.setOnClickListener {
            val intent = Intent(this@ViewSpecialistServicesAdmin, ViewSpecialistDetailServiceAdmin::class.java)
            intent.putExtra("nombreServicio", nombre)
            intent.putExtra("descripcionServicio", descripcion)
            intent.putExtra("precio", precio)
            intent.putExtra("imagenUrl", imagenUrl)
            intent.putExtra("categoria", categoria)
            startActivity(intent)
        }

        // Cargamos la imagen del servicio
        if (imagenUrl.isNotEmpty()) {
            Picasso.get().load(imagenUrl).into(imagenServicio)
        }

        // Agregamos la vista al contenedor
        serviciosContainer.addView(servicioView)
    }

    // Función que se ejecuta al presionar una card
    fun goDetail(view: View) {
        // Obtener el servicio seleccionado
        val cardView = view.parent as View
        val nombreServicio = cardView.findViewById<TextView>(R.id.edNombreServicio).text.toString()
        val descripcionServicio = cardView.findViewById<TextView>(R.id.edDescripcionServicio).text.toString()
        val precio = cardView.findViewById<TextView>(R.id.edPrecio).text.toString()
        val specialistId = intent.getStringExtra("specialistId")

        // Crear un Intent para la siguiente actividad
        val intent = Intent(this, ViewSpecialistDetailServiceAdmin::class.java)

        // Pasar los detalles del servicio como extras en el Intent
        intent.putExtra("nombreServicio", nombreServicio)
        intent.putExtra("descripcionServicio", descripcionServicio)
        intent.putExtra("precio", precio)
        intent.putExtra("specialistId", specialistId)

        // Iniciar la siguiente actividad
        startActivity(intent)
        finish()
    }

    // Función para volver al perfil del especialista
    private fun irAPerfilEspecialista() {
        finish()
    }

    // Función para volver al menú
    private fun irAlMenu() {
        val intent = Intent(this, MenuAdmin::class.java)
        startActivity(intent)
        finish()
    }
}
