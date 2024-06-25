package com.example.proyecto_fixit_final.Client

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto_fixit_final.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class ClientsComments : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var especialistaId: String
    private lateinit var servicioId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.clientes_comentarios)
        firestore = FirebaseFirestore.getInstance()

        // Obtener especialistaId y servicioId del Intent
        especialistaId = intent.getStringExtra("especialistaId") ?: ""
        servicioId = intent.getStringExtra("servicioId") ?: ""

        loadComments()
    }

    private fun loadComments() {
        firestore.collection("especialistas").document(especialistaId)
            .collection("servicios").document(servicioId)
            .collection("comentarios")
            .get()
            .addOnSuccessListener { result ->
                displayComments(result)
            }
            .addOnFailureListener {
                // Manejar errores
            }
    }

    private fun displayComments(comments: QuerySnapshot) {
        val comentariosContainer = findViewById<LinearLayout>(R.id.comentarios_container)
        comentariosContainer.removeAllViews()

        for (document in comments) {
            val comentario = document.getString("comentario") ?: ""
            val rating = document.getDouble("rating") ?: 0.0
            val nombreUsuario = document.getString("nombreUsuario") ?: "Anónimo"

            // Formatear el comentario con comillas y punto final
            val formattedComentario = "\"$comentario.\""

            val view = LayoutInflater.from(this).inflate(R.layout.card_comentario_cliente, null)
            val comentariosTextView = view.findViewById<TextView>(R.id.comentarios)
            val ratingBar = view.findViewById<RatingBar>(R.id.estrellas)
            val nombreUsuarioTextView = view.findViewById<TextView>(R.id.txt_nombrecliente)

            comentariosTextView.text = formattedComentario
            ratingBar.rating = rating.toFloat()
            nombreUsuarioTextView.text = nombreUsuario

            comentariosContainer.addView(view)
        }
    }

    fun goNewComment(view: View) {
        val intent = Intent(this, AddComments::class.java)
        intent.putExtra("especialistaId", especialistaId)
        intent.putExtra("servicioId", servicioId)
        startActivity(intent)
        finish()
    }
    fun backDetailService(view: View) {
        finish()
    }
}
