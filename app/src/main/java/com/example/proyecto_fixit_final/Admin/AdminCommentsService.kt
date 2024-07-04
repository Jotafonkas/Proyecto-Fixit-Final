package com.example.proyecto_fixit_final.Admin

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto_fixit_final.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class AdminCommentsService : AppCompatActivity() {

    private lateinit var comentariosContainer: LinearLayout
    private val TAG = "ViewCommentsServiceAdmin"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_comentarios_servicio)

        // Obtención de datos pasados por el intent
        val specialistId = intent.getStringExtra("specialistId")
        val serviceId = intent.getStringExtra("serviceId")
        val nombreServicio = intent.getStringExtra("nombreServicio") ?: ""

        // Referencias a las vistas
        comentariosContainer = findViewById(R.id.comentarios_container)

        if (!specialistId.isNullOrEmpty()) {
            if (!serviceId.isNullOrEmpty()) {
                // Buscar servicios del especialista y del servicio desde Firestore
                cargarComentarios(specialistId, serviceId, nombreServicio)
            }else {
                Toast.makeText(this, "No se pudo obtener el ID del servicio", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "No se pudo obtener el ID del especialista", Toast.LENGTH_SHORT).show()
        }

        // Configurar botones para volver al detalle del servicio y al menú
        val flechaVolver: ImageView = findViewById(R.id.flechavolver_detalleservicioadmin)
        val menu: TextView = findViewById(R.id.menu)

        flechaVolver.setOnClickListener { irADetallesServicio() }
        menu.setOnClickListener { irAlMenu() }
    }

    private fun cargarComentarios(specialistId: String, serviceId: String, nombreServicio:String?){
        // Obtener comentarios desde Firebase
        val db = FirebaseFirestore.getInstance()
        db.collection("especialistas")
            .document(specialistId)
            .collection("servicios")
            .whereEqualTo("nombreServicio", nombreServicio)
            .get()
            .addOnSuccessListener { serviceDocuments ->
                if (!serviceDocuments.isEmpty) {
                    for (serviceDocument in serviceDocuments) {
                        serviceDocument.reference.collection("comentarios").get().addOnSuccessListener { comments ->
                            if (!comments.isEmpty) {
                                displayComments(comments, serviceId, specialistId)
                            } else {
                                Log.d(TAG, "No comments found")
                            }
                        }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Error al obtener comentarios: ", e)
                            }
                    }
                } else {
                    Log.d(TAG, "No service found")
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error al obtener servicios: ", e)
            }
    }

    private fun displayComments(comments: QuerySnapshot, serviceId: String, specialistId: String) {
        for (comment in comments) {
            val comentarioText = comment.getString("comentario") ?: "Sin comentario"
            val rating = comment.getDouble("rating")?.toFloat() ?: 0f
            val nombreUsuario = comment.getString("nombreUsuario") ?: "Anónimo"

            val commentId = comment.id
            val commentView = LayoutInflater.from(this).inflate(R.layout.admin_card_comentarios_servicio, comentariosContainer, false)
            val btnEliminarComentario = commentView.findViewById<ImageButton>(R.id.btnEliminarComentario)

            val comentarioTextView = commentView.findViewById<TextView>(R.id.comentarios)
            val ratingBar = commentView.findViewById<RatingBar>(R.id.estrellas)
            val nombreUsuarioTextView = commentView.findViewById<TextView>(R.id.txt_nombrecliente)

            comentarioTextView.text = comentarioText
            ratingBar.rating = rating
            nombreUsuarioTextView.text = nombreUsuario


            // Establecer el tag del commentView con el commentId
            commentView.tag = commentId
            btnEliminarComentario.setOnClickListener {
                mostrarDialogoConfirmacion(commentView, serviceId, specialistId, commentId)
            }

            comentariosContainer.addView(commentView)
        }
    }

    // Función para mostrar un diálogo de confirmación antes de eliminar el comentario del servicio
    private fun mostrarDialogoConfirmacion(view: View, serviceId: String, specialistId: String, commentId: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Eliminar Comentario")
        builder.setMessage("¿Está seguro que desea eliminar este comentario?")

        // Si el usuario confirma la eliminación, se elimina el comentario
        builder.setPositiveButton("Sí") { dialog, _ ->
            eliminarComentario(view, serviceId, specialistId, commentId)
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

    // Función para eliminar el comentario de Firestore y de la vista
    private fun eliminarComentario(view: View, serviceId: String, specialistId: String, commentId: String) {
        val db = FirebaseFirestore.getInstance()
        // Eliminamos el comentario de Firestore
        db.collection("especialistas").document(specialistId).collection("servicios")
            .document(serviceId).collection("comentarios").document(commentId)
            .delete()
            .addOnSuccessListener {
                // Eliminamos la vista del comentario del contenedor
                comentariosContainer.removeView(view)
                Toast.makeText(this, "Comentario eliminado exitosamente.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al eliminar el comentario: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Función para volver al perfil del especialista
    private fun irADetallesServicio() {
        finish()
    }

    // Función para volver al menú
    private fun irAlMenu() {
        val intent = Intent(this, AdminMenu::class.java)
        startActivity(intent)
        finish()
    }
}