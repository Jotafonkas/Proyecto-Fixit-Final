package com.example.proyecto_fixit_final.Admin

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
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
    private lateinit var searchFilter: EditText
    private val especialistasList = mutableListOf<Map<String, String?>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ver_especialistas_admin)

        especialistasContainer = findViewById(R.id.lista_especialistas_container)
        searchFilter = findViewById(R.id.search_filter_specialists)

        // Buscar especialistas desde Firestore
        buscarEspecialistas()

        // Configurar botones para volver al menú
        val flechaVolverMenu: ImageView = findViewById(R.id.flechavolver_menu)
        val menu: TextView = findViewById(R.id.menu)

        flechaVolverMenu.setOnClickListener { irAlMenu() }
        menu.setOnClickListener { irAlMenu() }

        // Configurar filtro de búsqueda
        searchFilter.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filtrarEspecialistas(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun buscarEspecialistas() {
        db.collection("especialistas")
            .get()
            .addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
                if (task.isSuccessful) {
                    especialistasList.clear()
                    for (document in task.result) {
                        val especialista = mapOf(
                            "nombre" to document.getString("nombre"),
                            "imageUrl" to document.getString("imageUrl"),
                            "specialistId" to document.id,
                            "ciudad" to document.getString("ciudad"),
                            "rut" to document.getString("rut"),
                            "correo" to document.getString("correo"),
                            "telefono" to document.getString("telefono"),
                            "profesion" to document.getString("profesion")
                        )
                        if (!especialistasList.contains(especialista)) {
                            especialistasList.add(especialista)
                            addSpecialistCard(especialista)
                        }
                    }
                } else {
                    Log.w("ViewSpecialistsAdmin", "Error al obtener datos del especialista.", task.exception)
                }
            })
    }

    private fun addSpecialistCard(especialista: Map<String, String?>) {
        val inflater = LayoutInflater.from(this)
        val card = inflater.inflate(R.layout.card_lista_usuarios_admin, especialistasContainer, false)

        val nombreEspecialista: TextView = card.findViewById(R.id.nombreUsuario)
        val imagenEspecialista: ImageView = card.findViewById(R.id.imagen_usuario)

        nombreEspecialista.text = especialista["nombre"]

        if (!especialista["imageUrl"].isNullOrEmpty()) {
            Picasso.get().load(especialista["imageUrl"]).into(imagenEspecialista)
        } else {
            imagenEspecialista.setImageResource(R.drawable.imagen_login)
        }

        card.setOnClickListener {
            val intent = Intent(this, ProfileSpecialistAdmin::class.java).apply {
                putExtra("nombre", especialista["nombre"])
                putExtra("imageUrl", especialista["imageUrl"])
                putExtra("ciudad", especialista["ciudad"])
                putExtra("rut", especialista["rut"])
                putExtra("correo", especialista["correo"])
                putExtra("telefono", especialista["telefono"])
                putExtra("specialistId", especialista["specialistId"])
                putExtra("profesion", especialista["profesion"])
            }
            startActivity(intent)
        }

        especialistasContainer.addView(card)
    }

    private fun filtrarEspecialistas(query: String) {
        especialistasContainer.removeAllViews()
        val filteredList = especialistasList.filter {
            it["nombre"]?.contains(query, true) == true
        }
        for (especialista in filteredList) {
            addSpecialistCard(especialista)
        }
    }

    private fun irAlMenu() {
        val intent = Intent(this, MenuAdmin::class.java)
        startActivity(intent)
        finish()
    }
}
