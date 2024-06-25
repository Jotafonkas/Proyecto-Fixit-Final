package com.example.proyecto_fixit_final.Admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto_fixit_final.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.squareup.picasso.Picasso

class ViewSpecialistsAdmin : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var especialistasContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ver_especialistas_admin)

        especialistasContainer = findViewById(R.id.lista_especialistas_container)

        // Buscar especialistas desde Firestore
        buscarEspecialistas()

        // Configurar botones para volver al menú
        val flechaVolverMenu: ImageView = findViewById(R.id.flechavolver_menu)
        val menu: TextView = findViewById(R.id.menu)

        flechaVolverMenu.setOnClickListener { irAlMenu() }
        menu.setOnClickListener { irAlMenu() }
    }

    private fun buscarEspecialistas() {
        db.collection("especialistas")
            .get()
            .addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        val nombre = document.getString("nombre")
                        val imageUrl = document.getString("imageUrl")
                        val specialistId = document.id
                        val ciudad = document.getString("ciudad")
                        val rut = document.getString("rut")
                        val correo = document.getString("correo")
                        val telefono = document.getString("telefono")
                        val profesion = document.getString("profesion")
                        addSpecialistCard(nombre, imageUrl, specialistId, ciudad, rut, correo, telefono, profesion)
                    }
                } else {
                    Log.w("ViewSpecialistsAdmin", "Error al obtener datos del especialista.", task.exception)
                }
            })
    }

    private fun addSpecialistCard(nombre: String?, imageUrl: String?, specialistId: String, ciudad: String?, rut: String?, correo: String?, telefono: String?, profesion: String?) {
        val inflater = LayoutInflater.from(this)
        val card = inflater.inflate(R.layout.card_lista_usuarios_admin, especialistasContainer, false)

        val nombreEspecialista: TextView = card.findViewById(R.id.nombreUsuario)
        val imagenEspecialista: ImageView = card.findViewById(R.id.imagen_usuario)

        nombreEspecialista.text = nombre

        // Verificar si la URL de la imagen ha cambiado
        if (imageUrl != null && imageUrl.isNotEmpty()) {
            Picasso.get().load(imageUrl).into(imagenEspecialista)
        } else {
            // Si no hay imagen, cargar la imagen predeterminada
            imagenEspecialista.setImageResource(R.drawable.imagen_login)
        }

        // Establecer el listener del card para iniciar ProfileSpecialistAdmin con los datos del especialista
        card.setOnClickListener {
            val intent = Intent(this, ProfileSpecialistAdmin::class.java)
            intent.putExtra("nombre", nombre)
            intent.putExtra("imageUrl", imageUrl)
            intent.putExtra("ciudad", ciudad)
            intent.putExtra("rut", rut)
            intent.putExtra("correo", correo)
            intent.putExtra("telefono", telefono)
            intent.putExtra("specialistId", specialistId)
            intent.putExtra("profesion", profesion)
            startActivity(intent)
        }

        especialistasContainer.addView(card)
    }

    // Función para volver al menú
    private fun irAlMenu() {
        val intent = Intent(this, MenuAdmin::class.java)
        startActivity(intent)
        finish()
    }
}
