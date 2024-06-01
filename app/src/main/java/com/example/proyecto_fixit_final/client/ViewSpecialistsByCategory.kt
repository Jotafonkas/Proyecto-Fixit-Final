package com.example.proyecto_fixit_final.Specialist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_fixit_final.R
import com.example.proyecto_fixit_final.adapters.SpecialistAdapter
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Toast
import com.example.proyecto_fixit_final.Specialist.modelos.Specialist

class ViewSpecialistsByCategory : AppCompatActivity() {

    private lateinit var categoryName: String
    private lateinit var recyclerView: RecyclerView
    private val specialistsList = mutableListOf<Specialist>() // Lista de especialistas que se mostrarán en el RecyclerView
    private lateinit var adapter: SpecialistAdapter

    // Inicializamos la actividad y cargar los especialistas de la categoría seleccionada
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_specialist_client)

        categoryName = intent.getStringExtra("categoryName") ?: "" // Obtenemos el nombre de la categoría seleccionada
        recyclerView = findViewById(R.id.recycler_view_specialists) // Referenciamos el RecyclerView en el layout
        recyclerView.layoutManager = LinearLayoutManager(this) // Configuramos el RecyclerView con un LinearLayoutManager

        adapter = SpecialistAdapter(specialistsList) // Creamos un adaptador para el RecyclerView
        recyclerView.adapter = adapter // Asignamos el adaptador al RecyclerView

        loadSpecialistsByCategory()
    }

    // Cargamos los especialistas de la categoría seleccionada
    private fun loadSpecialistsByCategory() {
        val db = FirebaseFirestore.getInstance()

        // Obtenemos los especialistas de la colección "especialistas" y sus servicios de la categoría seleccionada
        db.collection("especialistas")
            .get()
            .addOnSuccessListener { specialistDocuments ->
                // Iteramos sobre los documentos de los especialistas
                for (specialistDocument in specialistDocuments) {
                    val nombre = specialistDocument.getString("nombre") ?: ""
                    val imageUrl = specialistDocument.getString("imageUrl") ?: ""
                    val specialistUid = specialistDocument.id

                    // Obtenemos los servicios de la categoría seleccionada del especialista
                    db.collection("especialistas")
                        .document(specialistUid)
                        .collection("servicios")
                        .whereEqualTo("categoria", categoryName)
                        .get()
                        .addOnSuccessListener { serviceDocuments ->
                            for (serviceDocument in serviceDocuments) {
                                val nombreServicio = serviceDocument.getString("nombreServicio") ?: ""
                                val categoria = serviceDocument.getString("categoria") ?: ""

                                // Creamos un objeto Specialist con los datos obtenidos y lo añadimos a la lista de especialistas
                                val specialist = Specialist(
                                    nombre = nombre,
                                    imageUrl = imageUrl,
                                    nombreServicio = nombreServicio,
                                    categoria = categoria
                                )
                                // Añadimos el especialista a la lista y notificamos al adaptador
                                specialistsList.add(specialist)
                                adapter.notifyDataSetChanged()
                            }
                        }
                        // Manejamos errores al cargar los servicios del especialista de la categoría seleccionada
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error al cargar los servicios: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            // Manejamos errores al cargar los especialistas
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar los especialistas: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
