package com.example.proyecto_fixit_final.Specialist

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_fixit_final.R
import com.example.proyecto_fixit_final.Specialist.modelos.Specialist
import com.example.proyecto_fixit_final.adapters.SpecialistAdapter
import com.google.android.material.chip.ChipGroup
import com.google.firebase.firestore.FirebaseFirestore

class ViewSpecialistsByCategory : AppCompatActivity() {

    private lateinit var categoryName: String
    private lateinit var recyclerView: RecyclerView
    private val specialistsList = mutableListOf<Specialist>() // Lista de especialistas que se mostrarán en el RecyclerView
    private lateinit var adapter: SpecialistAdapter
    private lateinit var chipGroup: ChipGroup
    private var isPriceAscending = true
    private var isCityAscending = true
    private var isNameAscending = true
    private var lastCheckedChipId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_specialist_client)

        categoryName = intent.getStringExtra("categoryName") ?: "" // Obtenemos el nombre de la categoría seleccionada
        recyclerView = findViewById(R.id.recycler_view_specialists) // Referenciamos el RecyclerView en el layout
        chipGroup = findViewById(R.id.chip_group) // Referenciamos el ChipGroup

        recyclerView.layoutManager = LinearLayoutManager(this) // Configuramos el RecyclerView con un LinearLayoutManager
        adapter = SpecialistAdapter(specialistsList) // Creamos un adaptador para el RecyclerView
        recyclerView.adapter = adapter // Asignamos el adaptador al RecyclerView

        loadSpecialistsByCategory()
        setupChipGroupListener()
    }

    private fun setupChipGroupListener() {
        chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isEmpty()) return@setOnCheckedStateChangeListener

            val checkedId = checkedIds.firstOrNull()
            if (checkedId == lastCheckedChipId) {
                // Si se presiona el mismo chip dos veces, alternar el orden
                when (checkedId) {
                    R.id.chip_precio -> isPriceAscending = !isPriceAscending
                    R.id.chip_ciudad -> isCityAscending = !isCityAscending
                    R.id.chip_nombre -> isNameAscending = !isNameAscending
                }
            } else {
                // Si se presiona un chip diferente, restablecer las banderas
                isPriceAscending = true
                isCityAscending = true
                isNameAscending = true
                lastCheckedChipId = checkedId
            }

            when (checkedId) {
                R.id.chip_ciudad -> sortSpecialistsByCity()
                R.id.chip_nombre -> sortSpecialistsByName()
                R.id.chip_precio -> sortSpecialistsByPrice()
            }

            adapter.notifyDataSetChanged() // Notificamos al adaptador que los datos han cambiado
        }
    }

    private fun sortSpecialistsByCity() {
        if (isCityAscending) {
            specialistsList.sortBy { it.ciudad.lowercase() }
        } else {
            specialistsList.sortByDescending { it.ciudad.lowercase() }
        }
    }

    private fun sortSpecialistsByName() {
        if (isNameAscending) {
            specialistsList.sortBy { it.nombre.lowercase() }
        } else {
            specialistsList.sortByDescending { it.nombre.lowercase() }
        }
    }

    private fun sortSpecialistsByPrice() {
        if (isPriceAscending) {
            specialistsList.sortBy { it.precio.toDoubleOrNull() ?: 0.0 }
        } else {
            specialistsList.sortByDescending { it.precio.toDoubleOrNull() ?: 0.0 }
        }
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
                    val ciudad = specialistDocument.getString("ciudad") ?: "" // Obtener la ciudad del especialista
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
                                val precio = serviceDocument.getString("precio") ?: "" // Obtener el precio del servicio

                                val specialist = Specialist(
                                    uid = specialistUid,
                                    nombre = nombre,
                                    imageUrl = imageUrl,
                                    nombreServicio = nombreServicio,
                                    categoria = categoria,
                                    ciudad = ciudad, // Añadir la ciudad al objeto Specialist
                                    precio = precio // Añadir el precio al objeto Specialist
                                )
                                specialistsList.add(specialist)
                            }
                            adapter.notifyDataSetChanged() // Notificamos al adaptador después de añadir todos los especialistas
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

    // Función para volver al menú
    fun backMenu(view: View) {
        super.onBackPressed()
    }
}
