package com.example.proyecto_fixit_final.Specialist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_fixit_final.R
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Toast
import com.example.proyecto_fixit_final.Specialist.modelos.Specialist
import com.example.proyecto_fixit_final.adapters.SpecialistAdapter

class ViewSpecialistsByCategory : AppCompatActivity() {

    private lateinit var categoryName: String
    private lateinit var recyclerView: RecyclerView
    private val specialistsList = mutableListOf<Specialist>() // Lista de especialistas que se mostrarán en el RecyclerView
    private lateinit var adapter: SpecialistAdapter

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

        db.collection("especialistas")
            .get()
            .addOnSuccessListener { specialistDocuments ->
                for (specialistDocument in specialistDocuments) {
                    val nombre = specialistDocument.getString("nombre") ?: ""
                    val imageUrl = specialistDocument.getString("imageUrl") ?: ""
                    val specialistUid = specialistDocument.id

                    db.collection("especialistas")
                        .document(specialistUid)
                        .collection("servicios")
                        .whereEqualTo("categoria", categoryName)
                        .get()
                        .addOnSuccessListener { serviceDocuments ->
                            for (serviceDocument in serviceDocuments) {
                                val nombreServicio = serviceDocument.getString("nombreServicio") ?: ""
                                val categoria = serviceDocument.getString("categoria") ?: ""

                                val specialist = Specialist(
                                    uid = specialistUid,
                                    nombre = nombre,
                                    imageUrl = imageUrl,
                                    nombreServicio = nombreServicio,
                                    categoria = categoria
                                )
                                specialistsList.add(specialist)
                                adapter.notifyDataSetChanged()
                            }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error al cargar los servicios: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar los especialistas: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
