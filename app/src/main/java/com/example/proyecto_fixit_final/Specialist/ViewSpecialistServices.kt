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
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import android.app.AlertDialog
import com.example.proyecto_fixit_final.NavBar
import java.text.NumberFormat
import java.util.Locale

class ViewSpecialistServices : AppCompatActivity() {
    private lateinit var serviciosContainer: LinearLayout
    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ver_servicios_especialista)

        uid = intent.getStringExtra("uid").toString()
        if (uid.isEmpty()) {
            Toast.makeText(this, "Error: no se proporcionó un UID de usuario.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        serviciosContainer = findViewById(R.id.servicios_container)
        cargarServicios()
    }

    fun backMenu(view: View) {
        super.onBackPressed()
        finish()
    }

    private fun cargarServicios() {
        val db = FirebaseFirestore.getInstance()
        db.collection("especialistas").document(uid).collection("servicios")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val nombreServicio = document.getString("nombreServicio") ?: ""
                    val descripcionServicio = document.getString("descripcionServicio") ?: ""
                    val precio = document.getString("precio") ?: ""
                    val imagenUrl = document.getString("imagenUrl") ?: ""
                    val categoriaServicio = document.getString("categoriaServicio") ?: ""
                    val estado = document.getString("estado") ?: "Pendiente"
                    val documentId = document.id

                    agregarServicio(nombreServicio, descripcionServicio, precio, imagenUrl, categoriaServicio, estado, documentId)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al cargar servicios: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun agregarServicio(nombre: String, descripcion: String, precio: String, imagenUrl: String, categoria: String, estado: String, documentId: String) {
        val servicioView = LayoutInflater.from(this).inflate(R.layout.item_servicio, serviciosContainer, false)
        val imagenServicio = servicioView.findViewById<ImageView>(R.id.imagen_servicio)
        val txtNombreServicio = servicioView.findViewById<TextView>(R.id.edNombreServicio)
        val txtDescripcionServicio = servicioView.findViewById<TextView>(R.id.edDescripcionServicio)
        val txtPrecioServicio = servicioView.findViewById<TextView>(R.id.edPrecio)
        val txtEstadoServicio = servicioView.findViewById<TextView>(R.id.estadoservicio)

        val btnEliminarServicio = servicioView.findViewById<ImageButton>(R.id.btnEliminarServicio)

        // Formatear el precio
        val formattedPrice = formatPrice(precio)

        txtNombreServicio.text = nombre
        txtDescripcionServicio.text = descripcion
        txtPrecioServicio.text = formattedPrice
        txtEstadoServicio.text = estado

        txtEstadoServicio.setTextColor(
            if (estado == "Verificado") resources.getColor(R.color.green)
            else resources.getColor(R.color.yellow)
        )

        servicioView.setOnClickListener {
            val intent = Intent(this@ViewSpecialistServices, ViewSpecialistDetailService::class.java)
            intent.putExtra("nombreServicio", nombre)
            intent.putExtra("descripcionServicio", descripcion)
            intent.putExtra("precio", formattedPrice)
            intent.putExtra("imagenUrl", imagenUrl)
            intent.putExtra("categoria", categoria)
            intent.putExtra("estado", estado)
            intent.putExtra("uid", uid)
            startActivity(intent)
        }

        if (imagenUrl.isNotEmpty()) {
            Picasso.get().load(imagenUrl).into(imagenServicio)
        }

        servicioView.tag = documentId

        btnEliminarServicio.setOnClickListener {
            mostrarDialogoConfirmacion(servicioView, documentId)
        }

        serviciosContainer.addView(servicioView)
    }

    private fun formatPrice(price: String): String {
        return try {
            val parsedPrice = price.toDouble()
            val formatter = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
            formatter.format(parsedPrice)
        } catch (e: NumberFormatException) {
            price
        }
    }


    fun goDetail(view: View) {
        val cardView = view.parent as View
        val nombreServicio = cardView.findViewById<TextView>(R.id.edNombreServicio).text.toString()
        val descripcionServicio = cardView.findViewById<TextView>(R.id.edDescripcionServicio).text.toString()
        val precio = cardView.findViewById<TextView>(R.id.edPrecio).text.toString()

        val intent = Intent(this, ViewSpecialistDetailService::class.java)
        intent.putExtra("nombreServicio", nombreServicio)
        intent.putExtra("descripcionServicio", descripcionServicio)
        intent.putExtra("precio", precio)
        intent.putExtra("uid", uid)

        startActivity(intent)
    }

    private fun mostrarDialogoConfirmacion(view: View, documentId: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Eliminar Servicio")
        builder.setMessage("¿Está seguro que desea eliminar este servicio?")

        builder.setPositiveButton("Sí") { dialog, _ ->
            eliminarServicio(view, documentId)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun eliminarServicio(view: View, documentId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("especialistas").document(uid).collection("servicios").document(documentId)
            .delete()
            .addOnSuccessListener {
                db.collection("admin").document("solicitudes").collection("solicitud").document(documentId)
                    .delete()
                    .addOnSuccessListener {
                        serviciosContainer.removeView(view)
                        Toast.makeText(this, "Servicio eliminado exitosamente.", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al eliminar la solicitud: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al eliminar el servicio: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
