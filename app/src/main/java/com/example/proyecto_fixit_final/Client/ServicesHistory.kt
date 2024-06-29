package com.example.proyecto_fixit_final.Client

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_fixit_final.R
import com.example.proyecto_fixit_final.adapters.ServiceAdapter
import com.example.proyecto_fixit_final.modelos.Services
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ServicesHistory : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val servicesList = mutableListOf<Services>()
    private lateinit var adapter: ServiceAdapter
    private lateinit var firestore: FirebaseFirestore

    companion object {
        private const val TAG = "ServicesHistory"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.historial_servicios)

        recyclerView = findViewById(R.id.recycler_historial)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ServiceAdapter(servicesList)
        recyclerView.adapter = adapter

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
                servicesList.clear() // Limpiamos la lista antes de agregar nuevos datos
                for (document in documents) {
                    val nombre = document.getString("nombreEspecialista") ?: ""
                    val imageUrl = document.getString("imagenUrl") ?: ""
                    val uidServicio = document.getString("UidServicio") ?: ""
                    val nombreServicio = document.getString("nombreServicio") ?: ""
                    val categoria = document.getString("categoria") ?: ""
                    val valor = document.getString("precio") ?: ""

                    val servicio = Services(
                        uid = uidServicio,
                        nombre = nombre,
                        imageUrl = imageUrl,
                        nombreServicio = nombreServicio,
                        categoria = categoria,
                        precio = valor
                    )
                    servicesList.add(servicio)
                    Log.d(TAG, "Servicio añadido: $nombre, $nombreServicio")
                }
                adapter.notifyDataSetChanged()
                Log.d(TAG, "Total de servicios cargados: ${servicesList.size}")
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar los servicios: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Error al cargar los servicios", e)
            }
    }
    fun backMenu(view: View) {
        super.onBackPressed()
    }
}
