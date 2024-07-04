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
import java.text.NumberFormat
import java.util.Locale

class SpecialistDetailService : AppCompatActivity() {

    private var uid: String? = null
    private var nombreServicio: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.especialista_detalle_servicios)

        uid = intent.getStringExtra("uid")
        nombreServicio = intent.getStringExtra("nombreServicio")

        if (uid.isNullOrEmpty() || nombreServicio.isNullOrEmpty()) {
            Log.e("ViewSpecialistDetail", "Detalles del servicio no encontrados en el Intent")
            finish()
            return
        }

        val descripcionServicio = intent.getStringExtra("descripcionServicio")
        val precio = intent.getStringExtra("precio")
        val imagenUrl = intent.getStringExtra("imagenUrl")
        val categoriaServicio = intent.getStringExtra("categoria")

        val txtNombreServicio = findViewById<TextView>(R.id.nombre_servicio)
        val txtDescripcion = findViewById<TextView>(R.id.descripcion_servicio)
        val txtPrecio = findViewById<TextView>(R.id.precio_servicio)
        val txtCategoria = findViewById<TextView>(R.id.categoria_servicio)
        val imageServiceSpecialist = findViewById<ImageView>(R.id.imageServiceSpecialist)

        txtNombreServicio.text = nombreServicio
        txtDescripcion.text = descripcionServicio
        txtPrecio.text = formatPrice(precio)
        txtCategoria.text = categoriaServicio

        if (!imagenUrl.isNullOrEmpty()) {
            Picasso.get().load(imagenUrl).into(imageServiceSpecialist)
        } else {
            Log.e("ViewSpecialistDetail", "URL de imagen no disponible")
        }

        val db = FirebaseFirestore.getInstance()
        db.collection("especialistas").document(uid!!)
            .collection("servicios")
            .whereEqualTo("nombreServicio", nombreServicio)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val estado = document.getString("estado") ?: "Pendiente"
                    val txtEstadoServicio = findViewById<TextView>(R.id.estadoservicio)
                    txtEstadoServicio.text = estado
                    txtEstadoServicio.setTextColor(
                        if (estado == "Verificado") resources.getColor(R.color.green)
                        else resources.getColor(R.color.yellow)
                    )

                    // Actualizar la imagen y la categoría del servicio en caso de que no estén disponibles en el Intent
                    val imagenUrl = document.getString("imagenUrl")
                    val categoriaServicio = document.getString("categoria")

                    if (!imagenUrl.isNullOrEmpty()) {
                        Picasso.get().load(imagenUrl).into(imageServiceSpecialist)
                    }

                    if (!categoriaServicio.isNullOrEmpty()) {
                        txtCategoria.text = categoriaServicio
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("ViewSpecialistDetail", "Error al obtener detalles del servicio", exception)
            }
    }

    private fun formatPrice(price: String?): String {
        return if (!price.isNullOrEmpty()) {
            try {
                val parsedPrice = price.toDouble()
                val formatter = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
                formatter.format(parsedPrice)
            } catch (e: NumberFormatException) {
                price
            }
        } else {
            "Precio no disponible"
        }
    }

    fun backServices(view: View) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            val intent = Intent(this, SpecialistServices::class.java)
            intent.putExtra("uid", uid)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Error: Usuario no autenticado.", Toast.LENGTH_SHORT).show()
        }
    }

    fun goToComments(view: View) {
        val intent = Intent(this, SpecialistClientsComments::class.java)
        intent.putExtra("uid", uid)
        intent.putExtra("nombreServicio", nombreServicio)
        startActivity(intent)
    }

}
