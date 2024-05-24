package com.example.proyecto_fixit_final.Specialist

import android.os.Bundle
import android.util.Log
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto_fixit_final.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class ViewSpecialistComments : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ver_comentarios_especialista)

        loadComentarios()
    }

    private fun loadComentarios() {
        db.collection("comentarios")
            .get()
            .addOnSuccessListener { result ->
                val comentarios = result.map { it.toObject(Comentario::class.java) }

                // Asigna los datos a las vistas
                if (comentarios.size > 0) {
                    findViewById<TextView>(R.id.txt_nombrecliente1).text = comentarios[0].nombre
                    findViewById<TextView>(R.id.comentarios1).text = comentarios[0].comentario
                    findViewById<RatingBar>(R.id.estrellas1).rating = comentarios[0].estrellas.toFloat()
                }

                if (comentarios.size > 1) {
                    findViewById<TextView>(R.id.txt_nombrecliente2).text = comentarios[1].nombre
                    findViewById<TextView>(R.id.comentarios2).text = comentarios[1].comentario
                    findViewById<RatingBar>(R.id.estrellas2).rating = comentarios[1].estrellas.toFloat()
                }

                if (comentarios.size > 2) {
                    findViewById<TextView>(R.id.txt_nombrecliente3).text = comentarios[2].nombre
                    findViewById<TextView>(R.id.comentarios3).text = comentarios[2].comentario
                    findViewById<RatingBar>(R.id.estrellas3).rating = comentarios[2].estrellas.toFloat()
                }
            }
            .addOnFailureListener { exception ->
                // Manejo de errores
                exception.printStackTrace()
            }
    }
}
