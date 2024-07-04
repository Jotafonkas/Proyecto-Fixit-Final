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

class AdminAuthSpecialist : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var especialistasContainer: LinearLayout
    private val especialistasList = mutableListOf<Map<String, Any?>>()

    companion object {
        const val AUTHORIZATION_UPDATE_REQUEST = 100 // Código de solicitud para la actualización de autorización
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_auth_specialist)

        especialistasContainer = findViewById(R.id.lista_especialistas_container)
        buscarEspecialistas()

        val flechaVolverMenu: ImageView = findViewById(R.id.flechavolver_menu)
        val menu: TextView = findViewById(R.id.menu)

        flechaVolverMenu.setOnClickListener { irAlMenu() }
        menu.setOnClickListener { irAlMenu() }
    }

    private fun buscarEspecialistas() {
        db.collection("especialistas")
            .whereEqualTo("verified", "unverified")
            .get()
            .addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
                if (task.isSuccessful) {
                    especialistasList.clear()
                    especialistasContainer.removeAllViews() // Limpiar el contenedor antes de agregar nuevas vistas
                    for (document in task.result!!) {
                        val especialista = mapOf(
                            "nombre" to document.getString("nombre"),
                            "imageUrl" to document.getString("imageUrl"),
                            "specialistId" to document.id,
                            "ciudad" to document.getString("ciudad"),
                            "rut" to document.getString("rut"),
                            "correo" to document.getString("correo"),
                            "telefono" to document.getString("telefono"),
                            "profesion" to document.getString("profesion"),
                            "autorizado" to document.getBoolean("autorizado"),
                            "verified" to document.getString("verified")
                        )
                        if (!especialistasList.contains(especialista)) {
                            especialistasList.add(especialista)
                            addSpecialistCard(especialista)
                        }
                    }
                } else {
                    Log.w("AuthSpecialist", "Error al obtener datos del especialista.", task.exception)
                }
            })
    }


    private fun addSpecialistCard(especialista: Map<String, Any?>) {
        val inflater = LayoutInflater.from(this)
        val card = inflater.inflate(R.layout.admin_card_lista_usuarios_autorizacion, especialistasContainer, false)

        val nombreEspecialista: TextView = card.findViewById(R.id.nombreUsuario)
        val imagenEspecialista: ImageView = card.findViewById(R.id.imagen_usuario)

        nombreEspecialista.text = especialista["nombre"] as String?

        val imageUrl = especialista["imageUrl"] as String?
        if (!imageUrl.isNullOrEmpty()) {
            Picasso.get().load(imageUrl).into(imagenEspecialista)
        } else {
            imagenEspecialista.setImageResource(R.drawable.imagen_login)
        }

        card.setOnClickListener {
            val intent = Intent(this, AdminAuthSpecialistProfile::class.java).apply {
                putExtra("nombre", especialista["nombre"] as String?)
                putExtra("imageUrl", especialista["imageUrl"] as String?)
                putExtra("ciudad", especialista["ciudad"] as String?)
                putExtra("rut", especialista["rut"] as String?)
                putExtra("correo", especialista["correo"] as String?)
                putExtra("telefono", especialista["telefono"] as String?)
                putExtra("specialistId", especialista["specialistId"] as String?)
                putExtra("profesion", especialista["profesion"] as String?)
            }
            startActivityForResult(intent, AUTHORIZATION_UPDATE_REQUEST)
        }

        especialistasContainer.addView(card)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTHORIZATION_UPDATE_REQUEST && resultCode == RESULT_OK) {
            Log.d("AuthSpecialist", "Actualizando lista de especialistas no autorizados")
            especialistasContainer.removeAllViews()
            buscarEspecialistas()
        }
    }

    private fun irAlMenu() {
        val intent = Intent(this, AdminMenu::class.java)
        startActivity(intent)
    }
}
