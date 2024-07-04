package com.example.proyecto_fixit_final.Specialist

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_fixit_final.Client.Request
import com.example.proyecto_fixit_final.R
import com.example.proyecto_fixit_final.SpecialistRequestAdapter
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class SpecialistServicesRequest : AppCompatActivity() {
    private lateinit var recyclerViewSolicitudes: RecyclerView
    private lateinit var solicitudAdapter: SpecialistRequestAdapter
    private val solicitudesList = mutableListOf<Request>()
    private var uid: String? = null
    private lateinit var firestore: FirebaseFirestore

    companion object {
        private const val TAG = "ServicesRequest"
        private const val COLLECTION_ESPECIALISTAS = "especialistas"
        private const val COLLECTION_CLIENTES = "clientes"
        private const val COLLECTION_SOLICITUDES_PENDIENTES = "SolicitudesPendientes"
        private const val COLLECTION_SERVICIOS_ACEPTADOS = "servicios_aceptados"
        private const val COLLECTION_SERVICIOS_SOLICITADOS = "servicios_solicitados"
        private const val COLLECTION_SERVICIOS = "servicios"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.especialista_solicitud_servicios)

        recyclerViewSolicitudes = findViewById(R.id.recyclerViewSolicitudes)
        recyclerViewSolicitudes.layoutManager = LinearLayoutManager(this)
        solicitudAdapter = SpecialistRequestAdapter(solicitudesList, this::onAcceptClick, this::onRejectClick)
        recyclerViewSolicitudes.adapter = solicitudAdapter

        firestore = FirebaseFirestore.getInstance()

        uid = intent.getStringExtra("uid")
        if (uid.isNullOrEmpty()) {
            Log.e(TAG, "UID del usuario no encontrado en el Intent")
            finish()
            return
        }
        cargarSolicitudes(uid!!)
    }

    private fun cargarSolicitudes(uid: String) {
        firestore.collection(COLLECTION_ESPECIALISTAS).document(uid).collection(COLLECTION_SOLICITUDES_PENDIENTES)
            .get()
            .addOnSuccessListener { documents ->
                solicitudesList.clear()
                solicitudesList.addAll(documents.map { it.toRequest() })
                solicitudAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                showToastAndLog("Error al cargar solicitudes", exception)
            }
    }

    private fun onAcceptClick(solicitud: Request) {
        val especialistaRef = firestore.collection(COLLECTION_ESPECIALISTAS).document(uid!!)
        val clienteRef = firestore.collection(COLLECTION_CLIENTES).document(solicitud.clienteId)

        especialistaRef.collection(COLLECTION_SERVICIOS).document(solicitud.servicioId).get()
            .addOnSuccessListener { servicioDocument ->
                if (servicioDocument.exists()) {
                    val acceptedService = servicioDocument.toAcceptedService(solicitud)
                    guardarSolicitudAceptada(especialistaRef, clienteRef, solicitud.id, acceptedService)
                } else {
                    showToastAndLog("El servicio no existe")
                }
            }
            .addOnFailureListener { e ->
                showToastAndLog("Error al obtener el servicio", e)
            }
    }

    private fun guardarSolicitudAceptada(especialistaRef: DocumentReference, clienteRef: DocumentReference, solicitudId: String, acceptedService: Map<String, Any>) {
        especialistaRef.collection(COLLECTION_SERVICIOS_ACEPTADOS).document(solicitudId).set(acceptedService)
            .addOnSuccessListener {
                eliminarSolicitudPendiente(especialistaRef, clienteRef, solicitudId, acceptedService)
            }
            .addOnFailureListener { e ->
                showToastAndLog("Error al guardar la solicitud aceptada en el especialista", e)
            }
    }


    private fun eliminarSolicitudPendiente(especialistaRef: DocumentReference, clienteRef: DocumentReference, solicitudId: String, acceptedService: Map<String, Any>) {
        especialistaRef.collection(COLLECTION_SOLICITUDES_PENDIENTES).document(solicitudId).delete()
            .addOnSuccessListener {
                clienteRef.collection(COLLECTION_SERVICIOS_SOLICITADOS).document(solicitudId).set(acceptedService)
                    .addOnSuccessListener {
                        clienteRef.collection(COLLECTION_SOLICITUDES_PENDIENTES).document(solicitudId).delete()
                            .addOnSuccessListener {
                                Toast.makeText(this, "Solicitud aceptada", Toast.LENGTH_SHORT).show()
                                updateRequestList()
                            }
                            .addOnFailureListener { e ->
                                showToastAndLog("Error al eliminar la solicitud pendiente del cliente", e)
                            }
                    }
                    .addOnFailureListener { e ->
                        showToastAndLog("Error al guardar la solicitud aceptada en el cliente", e)
                    }
            }
            .addOnFailureListener { e ->
                showToastAndLog("Error al eliminar la solicitud pendiente del especialista", e)
            }
    }


    private fun onRejectClick(solicitud: Request) {
        val especialistaRef = firestore.collection(COLLECTION_ESPECIALISTAS).document(uid!!)
        val clienteRef = firestore.collection(COLLECTION_CLIENTES).document(solicitud.clienteId)

        especialistaRef.collection(COLLECTION_SOLICITUDES_PENDIENTES).document(solicitud.id).delete()
            .addOnSuccessListener {
                clienteRef.collection(COLLECTION_SOLICITUDES_PENDIENTES).document(solicitud.id).delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Solicitud rechazada", Toast.LENGTH_SHORT).show()
                        updateRequestList()
                    }
                    .addOnFailureListener { e ->
                        showToastAndLog("Error al eliminar la solicitud pendiente del cliente", e)
                    }
            }
            .addOnFailureListener { e ->
                showToastAndLog("Error al rechazar la solicitud", e)
            }
    }

    private fun DocumentSnapshot.toRequest(): Request {
        return Request(
            id = getString("id") ?: "",
            clienteId = getString("clienteId") ?: "",
            nombreCliente = getString("nombreCliente") ?: "",
            servicioId = getString("servicioId") ?: "",
            nombreServicio = getString("nombreServicio") ?: "",
            estado = getString("estado") ?: "",
            nombreEspecialista = getString("nombreEspecialista") ?: "",
            descripcionServicio = getString("descripcion") ?: ""
        )
    }

    private fun DocumentSnapshot.toAcceptedService(solicitud: Request): Map<String, Any> {
        val nombreServicio = this.getString("nombreServicio") ?: ""
        val precio = this.getString("precio") ?: ""
        val imagenUrl = this.getString("imagenUrl") ?: ""
        val descripcion = this.getString("descripcionServicio") ?: ""

        return mapOf(
            "id" to solicitud.id,
            "clienteId" to solicitud.clienteId,
            "nombreCliente" to solicitud.nombreCliente,
            "nombreEspecialista" to solicitud.nombreEspecialista,
            "nombreServicio" to nombreServicio,
            "precio" to precio,
            "imagenUrl" to imagenUrl,
            "descripcion" to descripcion,
            "estado" to "aceptada"
        )
    }

    private fun showToastAndLog(message: String, exception: Exception? = null) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        Log.e(TAG, message, exception)
    }

    private fun updateRequestList() {
        cargarSolicitudes(uid!!)
    }

    fun goBack(view: View) {
        super.onBackPressed()
    }
}
