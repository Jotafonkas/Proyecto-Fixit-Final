package com.example.proyecto_fixit_final.Client

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_fixit_final.R
import com.example.proyecto_fixit_final.adapters.ServiceAdapter
import com.example.proyecto_fixit_final.modelos.Services
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ServicesHistory : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val servicesList = mutableListOf<Services>()
    private val filteredServicesList = mutableListOf<Services>()
    private lateinit var adapter: ServiceAdapter
    private lateinit var firestore: FirebaseFirestore
    private lateinit var searchFilter: TextInputEditText

    companion object {
        private const val TAG = "ServicesHistory"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.historial_servicios)

        recyclerView = findViewById(R.id.recycler_historial)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ServiceAdapter(filteredServicesList)
        recyclerView.adapter = adapter

        searchFilter = findViewById(R.id.search_filter)
        searchFilter.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterServices(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        firestore = FirebaseFirestore.getInstance()

        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserUid != null) {
            loadUserRequestedServices(currentUserUid)
        } else {
            Toast.makeText(this, "No se encontró el usuario autenticado", Toast.LENGTH_SHORT).show()
            Log.e(TAG, "No se encontró el usuario autenticado")
        }
    }

    private fun loadUserRequestedServices(uid: String) {
        firestore.collection("clientes").document(uid).collection("servicios_solicitados")
            .get()
            .addOnSuccessListener { documents ->
                servicesList.clear()
                for (document in documents) {
                    val nombre = document.getString("nombreEspecialista") ?: ""
                    val imageUrl = document.getString("imagenUrl") ?: ""
                    val uidServicio = document.getString("UidServicio") ?: ""
                    val nombreServicio = document.getString("nombreServicio") ?: ""
                    val categoria = document.getString("categoria") ?: ""
                    val valor = document.getString("precio") ?: ""
                    val descripcion = document.getString("descripcion") ?: "" // Obtén la descripción

                    val servicio = Services(
                        uid = uidServicio,
                        nombre = nombre,
                        imageUrl = imageUrl,
                        nombreServicio = nombreServicio,
                        categoria = categoria,
                        precio = valor,
                        estado = document.getString("estado") ?: "", // Agrega el estado si es necesario
                        descripcionServicio = descripcion // Añade la descripción
                    )
                    servicesList.add(servicio)
                    Log.d(TAG, "Servicio añadido: $nombre, $nombreServicio")
                }
                filterServices(searchFilter.text.toString())
                Log.d(TAG, "Total de servicios cargados: ${servicesList.size}")
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar los servicios: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Error al cargar los servicios", e)
            }
    }


    private fun filterServices(query: String) {
        val lowerCaseQuery = query.lowercase().trim()
        filteredServicesList.clear()
        for (service in servicesList) {
            if (service.nombre.lowercase().contains(lowerCaseQuery) ||
                service.nombreServicio.lowercase().contains(lowerCaseQuery) ||
                service.precio.lowercase().contains(lowerCaseQuery)
            ) {
                filteredServicesList.add(service)
            }
        }
        adapter.notifyDataSetChanged()
        Log.d(TAG, "Servicios filtrados: ${filteredServicesList.size}")
    }

    fun backMenu(view: View) {
        super.onBackPressed()
    }
}
