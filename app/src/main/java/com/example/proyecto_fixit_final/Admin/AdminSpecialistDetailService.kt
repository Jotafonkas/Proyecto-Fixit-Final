package com.example.proyecto_fixit_final.Admin

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto_fixit_final.R
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class AdminSpecialistDetailService : AppCompatActivity() {

    private var nombreServicio: String? = null
    private lateinit var serviceId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_detalles_servicio_especialista)

        // Recuperar los detalles del servicio del Intent
        nombreServicio = intent.getStringExtra("nombreServicio")
        val descripcionServicio = intent.getStringExtra("descripcionServicio")
        val precio = intent.getStringExtra("precio")
        val specialistId = intent.getStringExtra("specialistId")
        if (!specialistId.isNullOrEmpty()) {
            // Buscar servicios del especialista desde Firestore
            cargarServicios(descripcionServicio, precio, specialistId)
        } else {
            Toast.makeText(this, "No se pudo obtener el ID del especialista", Toast.LENGTH_SHORT).show()
        }

        // Configurar botones para volver al perfil del especialista y al menú
        val flechaVolver: ImageView = findViewById(R.id.flechavolver_servicios)
        val menu: TextView = findViewById(R.id.menu)

        flechaVolver.setOnClickListener { irAServiciosEspecialista() }
        menu.setOnClickListener { irAlMenu() }
    }

    private fun cargarServicios(descripcionServicio: String?, precio: String?, specialistId: String) {

        // Encontrar las vistas
        val txtNombreServicio = findViewById<TextView>(R.id.nombre_servicio)
        val txtDescripcion = findViewById<TextView>(R.id.descripcion_servicio)
        val txtPrecio = findViewById<TextView>(R.id.precio_servicio)
        val imageServiceSpecialist = findViewById<ImageView>(R.id.imageServiceSpecialist)
        val btnEliminarServicio = findViewById<Button>(R.id.btnEliminarServicioAdmin)

        // Recuperar la imagenUrl y la categoría del servicio desde Firestore
        val db = FirebaseFirestore.getInstance()
        db.collection("especialistas").document(specialistId)
            .collection("servicios")
            .whereEqualTo("nombreServicio", nombreServicio)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val imagenUrl = document.getString("imagenUrl")
                    val categoriaServicio = document.getString("categoria")
                    serviceId = document.id // Guardar el ID del servicio

                    // Cargar la imagen en el ImageView usando Picasso
                    if (!imagenUrl.isNullOrEmpty()) {
                        Picasso.get().load(imagenUrl).into(imageServiceSpecialist)
                    }

                    // Establecer los datos en las vistas
                    txtNombreServicio.text = nombreServicio
                    txtDescripcion.text = descripcionServicio
                    txtPrecio.text = precio

                    // Mostrar la categoría en un TextView
                    val txtCategoria = findViewById<TextView>(R.id.categoria_servicio)
                    txtCategoria.text = categoriaServicio

                    // Función para el botón de eliminar servicio
                    btnEliminarServicio.setOnClickListener {
                        mostrarDialogoConfirmacion(serviceId, specialistId)
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Manejar el error
                Log.e("ViewSpecialistDetailServiceAdmin", "Error al obtener detalles del servicio", exception)
            }
    }

    // Función para mostrar un diálogo de confirmación antes de eliminar el servicio
    private fun mostrarDialogoConfirmacion(serviceId: String, specialistId: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Eliminar Servicio")
        builder.setMessage("¿Está seguro que desea eliminar este servicio?")

        // Si el usuario confirma la eliminación, se elimina el servicio
        builder.setPositiveButton("Sí") { dialog, _ ->
            eliminarServicio(specialistId, serviceId)
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
    private fun eliminarServicio(specialistId: String, serviceId: String) {
        val db = FirebaseFirestore.getInstance()
        // Eliminamos el servicio de Firestore
        db.collection("especialistas").document(specialistId).collection("servicios")
            .document(serviceId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Servicio eliminado exitosamente.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, AdminSpecialistServices::class.java)
                intent.putExtra("specialistId", specialistId)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al eliminar el servicio: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Función para ir a la sección de comentarios
    fun goToComments(view: View) {
        // Obtener el ID del especialista y de su servicio desde el Intent
        val specialistId = intent.getStringExtra("specialistId")
        val intent = Intent(this, AdminCommentsService::class.java)
        intent.putExtra("specialistId", specialistId)
        intent.putExtra("serviceId", serviceId)
        intent.putExtra("nombreServicio", nombreServicio)
        startActivity(intent)
    }

    // Función para volver al perfil del especialista
    private fun irAServiciosEspecialista() {
        val specialistId = intent.getStringExtra("specialistId")
        val intent = Intent(this, AdminSpecialistServices::class.java)
        intent.putExtra("specialistId", specialistId)
        startActivity(intent)
        finish()
    }

    // Función para volver al menú
    private fun irAlMenu() {
        val intent = Intent(this, AdminMenu::class.java)
        startActivity(intent)
        finish()
    }
}
