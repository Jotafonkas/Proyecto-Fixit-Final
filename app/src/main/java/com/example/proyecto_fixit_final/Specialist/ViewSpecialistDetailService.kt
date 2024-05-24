package com.example.proyecto_fixit_final.Specialist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto_fixit_final.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class ViewSpecialistDetailService : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ver_detalle_servicios_especialista)

        // Recuperar el UID del usuario del Intent
        val uid = intent.getStringExtra("uid")
        if (uid.isNullOrEmpty()) {
            // Manejar el caso en el que el UID esté vacío o nulo
            Log.e("ViewSpecialistDetail", "UID del usuario no encontrado en el Intent")
            finish() // Finalizar la actividad actual si no se encuentra el UID
            return
        }

        // Recuperar los detalles del servicio del Intent
        val nombreServicio = intent.getStringExtra("nombreServicio")
        val descripcionServicio = intent.getStringExtra("descripcionServicio")
        val precio = intent.getStringExtra("precio")

        // Encontrar las vistas en tu diseño XML
        val txtNombreServicio = findViewById<TextView>(R.id.nombre_servicio)
        val txtDescripcion = findViewById<TextView>(R.id.descripcion_servicio)
        val txtPrecio = findViewById<TextView>(R.id.precio_servicio)
        val imageServiceSpecialist = findViewById<ImageView>(R.id.imageServiceSpecialist)

        // Establecer los datos en las vistas
        txtNombreServicio.text = nombreServicio
        txtDescripcion.text = descripcionServicio
        txtPrecio.text = precio

        // Recuperar la imagenUrl y la categoría del servicio desde Firestore
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(uid)
            .collection("servicios")
            .whereEqualTo("nombreServicio", nombreServicio)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val imagenUrl = document.getString("imagenUrl")
                    val categoriaServicio = document.getString("categoria")

                    // Cargar la imagen en el ImageView usando Picasso
                    if (!imagenUrl.isNullOrEmpty()) {
                        Picasso.get().load(imagenUrl).into(imageServiceSpecialist)
                    }

                    // Mostrar la categoría en un TextView
                    val txtCategoria = findViewById<TextView>(R.id.categoria_servicio)
                    txtCategoria.text = categoriaServicio
                }
            }
            .addOnFailureListener { exception ->
                // Manejar el error
                Log.e("ViewSpecialistDetail", "Error al obtener detalles del servicio", exception)
            }
    }

    // Función para volver a los servicios
    fun backServices(view: View) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            val intent = Intent(this, ViewSpecialistServices::class.java)
            intent.putExtra("uid", uid)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Error: Usuario no autenticado.", Toast.LENGTH_SHORT).show()
        }
    }

    // Función para ir a la sección de comentarios
    fun goToComments(view: View) {
        val intent = Intent(this, ViewSpecialistComments::class.java)
        startActivity(intent)
    }

    // Función para ir a la sección de servicios
    fun goToServices(view: View) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            val intent = Intent(this, ViewSpecialistServices::class.java)
            intent.putExtra("uid", uid)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Error: Usuario no autenticado.", Toast.LENGTH_SHORT).show()
        }
    }
}
