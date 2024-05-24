package com.example.proyecto_fixit_final.Specialist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
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
import android.app.AlertDialog
import android.content.DialogInterface
import com.example.proyecto_fixit_final.NavBar

// Clase que muestra los servicios del especialista
class ViewSpecialistServices : AppCompatActivity() {
    private lateinit var serviciosContainer: LinearLayout
    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ver_servicios_especialista)

        // Obtener el UID del usuario
        uid = intent.getStringExtra("uid").toString()
        if (uid.isEmpty()) {
            Toast.makeText(this, "Error: no se proporcionó un UID de usuario.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Obtener el contenedor de servicios
        serviciosContainer = findViewById(R.id.servicios_container)
        cargarServicios()
    }

    // Función para regresar al menú principal
    fun backMenu(view: View) {
        val intent = Intent(this, NavBar::class.java)
        startActivity(intent)
        finish()
    }

    // Función para cargar los servicios del especialista
    private fun cargarServicios() {
        // Obtenemos la colección de servicios del especialista
        val db = FirebaseFirestore.getInstance()
        // Obtenemos los servicios del especialista
        db.collection("users").document(uid).collection("servicios")
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
        val servicioView = LayoutInflater.from(this).inflate(R.layout.item_servicio, serviciosContainer, false)
        val imagenServicio = servicioView.findViewById<ImageView>(R.id.imagen_servicio)
        val txtNombreServicio = servicioView.findViewById<TextView>(R.id.edNombreServicio)
        val txtDescripcionServicio = servicioView.findViewById<TextView>(R.id.edDescripcionServicio)
        val txtPrecioServicio = servicioView.findViewById<TextView>(R.id.edPrecio)
        val btnEliminarServicio = servicioView.findViewById<ImageButton>(R.id.btnEliminarServicio)

        // Establecemos valores del servicio en la vista
        txtNombreServicio.text = nombre
        txtDescripcionServicio.text = descripcion
        txtPrecioServicio.text = precio

        // Agregar clic listener para abrir la actividad de detalle con los datos del servicio
        servicioView.setOnClickListener {
            val intent = Intent(this@ViewSpecialistServices, ViewSpecialistDetailService::class.java)
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

        // Establecer el tag del servicioView con el documentId
        servicioView.tag = documentId
        btnEliminarServicio.setOnClickListener {
            mostrarDialogoConfirmacion(servicioView, documentId)
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

        // Crear un Intent para la siguiente actividad
        val intent = Intent(this, ViewSpecialistDetailService::class.java)

        // Pasar los detalles del servicio como extras en el Intent
        intent.putExtra("nombreServicio", nombreServicio)
        intent.putExtra("descripcionServicio", descripcionServicio)
        intent.putExtra("precio", precio)
        intent.putExtra("uid", uid)

        // Iniciar la siguiente actividad
        startActivity(intent)
    }

    // Función para mostrar un diálogo de confirmación antes de eliminar el servicio
    private fun mostrarDialogoConfirmacion(view: View, documentId: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Eliminar Servicio")
        builder.setMessage("¿Está seguro que desea eliminar este servicio?")

        // Si el usuario confirma la eliminación, se elimina el servicio
        builder.setPositiveButton("Sí") { dialog, _ ->
            eliminarServicio(view, documentId)
            dialog.dismiss()
        }

        // Si el usuario cancela la eliminación, se cierra el diálogo
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        // Mostramos el diálogo
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    // Función para eliminar el servicio de Firestore y de la vista
    private fun eliminarServicio(view: View, documentId: String) {
        val db = FirebaseFirestore.getInstance()
        // Eliminamos el servicio de Firestore
        db.collection("users").document(uid).collection("servicios").document(documentId)
            .delete()
            .addOnSuccessListener {
                // Eliminamos la vista del servicio del contenedor
                serviciosContainer.removeView(view)
                Toast.makeText(this, "Servicio eliminado exitosamente.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al eliminar el servicio: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
