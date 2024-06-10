package com.example.proyecto_fixit_final.Client

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.RatingBar
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto_fixit_final.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddComments : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var comentarioEditText: EditText
    private lateinit var ratingBar: RatingBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.agregar_comentarios)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        comentarioEditText = findViewById(R.id.comentario)
        ratingBar = findViewById(R.id.estrellas)
    }

    fun addComment(view: View) {
        val comentario = comentarioEditText.text.toString()
        val rating = ratingBar.rating
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid
            firestore.collection("clientes").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val nombreUsuario = document.getString("nombre") ?: "Anónimo"
                        saveComment(comentario, rating, nombreUsuario)
                    }
                }
                .addOnFailureListener {
                    // Manejar errores
                }
        }
    }

    private fun saveComment(comentario: String, rating: Float, nombreUsuario: String) {
        val commentData = hashMapOf(
            "comentario" to comentario,
            "rating" to rating,
            "nombreUsuario" to nombreUsuario
        )

        firestore.collection("comentarios")
            .add(commentData)
            .addOnSuccessListener {
                // Comentario agregado con éxito
                val intent = Intent(this, ClientsComments::class.java)
                startActivity(intent)
                finish() // Cerrar la actividad actual para que no esté en el stack
            }
            .addOnFailureListener {
                // Manejar errores
            }
    }
}
