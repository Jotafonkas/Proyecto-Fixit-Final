package com.example.proyecto_fixit_final.Specialist

import android.os.Bundle
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto_fixit_final.R
import com.google.firebase.firestore.FirebaseFirestore
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.google.firebase.firestore.QuerySnapshot

class ViewSpecialistComments : AppCompatActivity() {
    private lateinit var comentariosContainer: LinearLayout
    private val TAG = "ViewSpecialistComments"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ver_comentarios_especialista)

        // Obtención de datos pasados por el intent
        val uid = intent.getStringExtra("uid") ?: ""
        val nombreServicio = intent.getStringExtra("nombreServicio") ?: ""

        // Referencias a las vistas
        comentariosContainer = findViewById(R.id.comentarios_container)

        // Obtener comentarios desde Firebase
        val db = FirebaseFirestore.getInstance()
        db.collection("especialistas")
            .document(uid)
            .collection("servicios")
            .whereEqualTo("nombreServicio", nombreServicio)
            .get()
            .addOnSuccessListener { serviceDocuments ->
                if (!serviceDocuments.isEmpty) {
                    for (serviceDocument in serviceDocuments) {
                        serviceDocument.reference.collection("comentarios")
                            .get()
                            .addOnSuccessListener { comments ->
                                if (!comments.isEmpty) {
                                    displayComments(comments)
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

    private fun displayComments(comments: QuerySnapshot) {
        for (comment in comments) {
            val comentarioText = comment.getString("comentario") ?: "Sin comentario"
            val rating = comment.getDouble("rating")?.toFloat() ?: 0f
            val nombreUsuario = comment.getString("nombreUsuario") ?: "Anónimo"

            val commentView = LayoutInflater.from(this).inflate(R.layout.card_comentario_cliente, null)

            val comentarioTextView = commentView.findViewById<TextView>(R.id.comentarios)
            val ratingBar = commentView.findViewById<RatingBar>(R.id.estrellas)
            val nombreUsuarioTextView = commentView.findViewById<TextView>(R.id.txt_nombrecliente)

            comentarioTextView.text = comentarioText
            ratingBar.rating = rating
            nombreUsuarioTextView.text = nombreUsuario

            comentariosContainer.addView(commentView)
        }
    }

    fun goBack(view: View) {
        super.onBackPressed()
    }
}
