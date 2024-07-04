package com.example.proyecto_fixit_final.Admin

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto_fixit_final.R
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class AdminSpecialistServices : AppCompatActivity() {

    private lateinit var serviciosContainer: LinearLayout
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_servicios_especialista)

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

        // Registrar BroadcastReceiver para eliminar servicios de la UI
        val filter = IntentFilter("com.example.proyecto_fixit_final.SERVICIO_ELIMINADO")
        registerReceiver(servicioEliminadoReceiver, filter, RECEIVER_NOT_EXPORTED)
    }

    private val servicioEliminadoReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val documentId = intent?.getStringExtra("documentId")
            if (!documentId.isNullOrEmpty()) {
                eliminarServicioDeUI(documentId)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Desregistrar el BroadcastReceiver
        unregisterReceiver(servicioEliminadoReceiver)
    }

    // Función para cargar los servicios del especialista
    private fun cargarServicios(specialistId: String) {
        // Obtenemos la colección de servicios del especialista
        val db = FirebaseFirestore.getInstance()
        // Obtenemos los servicios del especialista que están en estado "Verificado"
        db.collection("especialistas").document(specialistId).collection("servicios")
            .whereEqualTo("estado", "Verificado")
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
        val servicioView = LayoutInflater.from(this).inflate(R.layout.admin_card_servicios_especialista, serviciosContainer, false)
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
            val intent = Intent(this@AdminSpecialistServices, AdminSpecialistDetailService::class.java)
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

        // Añadir una etiqueta al servicioView para poder identificarlo fácilmente
        servicioView.tag = documentId

        // Agregamos la vista al contenedor
        serviciosContainer.addView(servicioView)
    }

    // Función para eliminar un servicio del contenedor
    fun eliminarServicioDeUI(documentId: String) {
        for (i in 0 until serviciosContainer.childCount) {
            val view = serviciosContainer.getChildAt(i)
            if (view.tag == documentId) {
                serviciosContainer.removeViewAt(i)
                break
            }
        }
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
        val intent = Intent(this, AdminSpecialistDetailService::class.java)

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
        val intent = Intent(this, AdminMenu::class.java)
        startActivity(intent)
        finish()
    }
}
