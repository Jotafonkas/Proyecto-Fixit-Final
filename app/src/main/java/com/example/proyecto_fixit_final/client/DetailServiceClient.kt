package com.example.proyecto_fixit_final.Specialist

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto_fixit_final.R
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso
import android.widget.TextView
import android.widget.Toast
import com.example.proyecto_fixit_final.Client.ClientsComments
import com.example.proyecto_fixit_final.Client.PersonalProfileSpecialist
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class DetailServiceClient : AppCompatActivity() {
    private lateinit var especialistaId: String
    private lateinit var servicioId: String
    private lateinit var nombreEspecialista: String
    private lateinit var nombreServicio: String
    private lateinit var clienteId: String
    private lateinit var nombreCliente: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detalle_servicio_cliente)

        // Obtención de datos pasados por el intent
        especialistaId = intent.getStringExtra("uid") ?: ""
        nombreEspecialista = intent.getStringExtra("nombre") ?: ""
        nombreServicio = intent.getStringExtra("nombreServicio") ?: ""

        //se obtiene el id del usuario navegando
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        clienteId = currentUser.uid

        // Referencias a las vistas
        val imageView: ShapeableImageView = findViewById(R.id.imageServiceSpecialist)
        val nombreTextView: TextView = findViewById(R.id.input_nombre_especialista)
        val nombreServicioTextView: TextView = findViewById(R.id.input_nombre_servicio)
        val descripcionTextView: TextView = findViewById(R.id.input_descripcion_servicio)
        val btnSolicitud: Button = findViewById(R.id.botonsolicitar)

        // Asignación de datos a las vistas
        nombreTextView.text = nombreEspecialista
        nombreServicioTextView.text = nombreServicio

        // Obtener la descripción y la imagen del servicio desde Firebase
        val db = FirebaseFirestore.getInstance()
        db.collection("especialistas")
            .document(especialistaId)
            .collection("servicios")
            .whereEqualTo("nombreServicio", nombreServicio)
            .get()
            .addOnSuccessListener { serviceDocuments ->
                for (serviceDocument in serviceDocuments) {
                    val descripcionServicio = serviceDocument.getString("descripcionServicio") ?: ""
                    descripcionTextView.text = descripcionServicio
                    val imagenUrl = serviceDocument.getString("imagenUrl") ?: ""
                    Picasso.get().load(imagenUrl).into(imageView)
                    servicioId = serviceDocument.id // Guardar el ID del servicio
                }
            }
            .addOnFailureListener { e ->
                descripcionTextView.text = "Error al cargar la descripción: ${e.message}"
            }

        // Configurar el click listener para el botón de solicitud
        btnSolicitud.setOnClickListener {
            checkAndSendServiceRequest()
        }
    }

    fun goToProfileSpecialist(view: View) {
        val intent = Intent(this, PersonalProfileSpecialist::class.java)
        intent.putExtra("uid", especialistaId) // Pasa el uid del especialista
        startActivity(intent)
    }

    fun goToComments(view: View) {
        val intent = Intent(this, ClientsComments::class.java)
        intent.putExtra("especialistaId", especialistaId)
        intent.putExtra("servicioId", servicioId)
        startActivity(intent)
    }

    fun backServices(view: View) {
        onBackPressed()
    }

    private fun checkAndSendServiceRequest() {
        val db = FirebaseFirestore.getInstance()

        if (servicioId.isBlank() || nombreEspecialista.isBlank() || nombreServicio.isBlank()) {
            Toast.makeText(this, "Datos incompletos del servicio", Toast.LENGTH_SHORT).show()
            return
        }

        // Verificar si ya existe una solicitud para el mismo servicio y cliente
        db.collection("clientes")
            .document(clienteId)
            .collection("SolicitudesPendientes")
            .whereEqualTo("servicioId", servicioId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    Toast.makeText(this, "Ya has solicitado este servicio anteriormente", Toast.LENGTH_SHORT).show()
                } else {
                    sendServiceRequest()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al verificar solicitudes existentes: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun sendServiceRequest() {
        val db = FirebaseFirestore.getInstance()

        // Obtener el nombre del cliente desde Firestore
        db.collection("clientes")
            .document(clienteId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    nombreCliente = document.getString("nombre") ?: "Cliente Anónimo"

                    // Obtener el nombre del especialista y la descripción del servicio desde Firestore
                    db.collection("especialistas")
                        .document(especialistaId)
                        .get()
                        .addOnSuccessListener { specialistDocument ->
                            if (specialistDocument != null && specialistDocument.exists()) {
                                nombreEspecialista = specialistDocument.getString("nombre") ?: "Especialista Anónimo"

                                // Obtener la descripción del servicio
                                db.collection("especialistas")
                                    .document(especialistaId)
                                    .collection("servicios")
                                    .document(servicioId)
                                    .get()
                                    .addOnSuccessListener { serviceDocument ->
                                        val descripcionServicio = serviceDocument.getString("descripcionServicio") ?: "Sin descripción"

                                        val solicitudId = UUID.randomUUID().toString()

                                        val solicitud = hashMapOf(
                                            "id" to solicitudId,
                                            "clienteId" to clienteId,
                                            "nombreCliente" to nombreCliente,
                                            "servicioId" to servicioId,
                                            "nombreServicio" to nombreServicio,
                                            "nombreEspecialista" to nombreEspecialista,
                                            "descripcionServicio" to descripcionServicio, // Guardar descripción
                                            "estado" to "pendiente"
                                        )

                                        // Guardar solicitud en la subcolección del cliente
                                        db.collection("clientes")
                                            .document(clienteId)
                                            .collection("SolicitudesPendientes")
                                            .document(solicitudId)  // Usar el ID aleatorio
                                            .set(solicitud)
                                            .addOnSuccessListener {
                                                // Guardar solicitud en la subcolección del especialista
                                                db.collection("especialistas")
                                                    .document(especialistaId)
                                                    .collection("SolicitudesPendientes")
                                                    .document(solicitudId)  // Usar el ID aleatorio
                                                    .set(solicitud)
                                                    .addOnSuccessListener {
                                                        Toast.makeText(this, "Solicitud enviada con éxito", Toast.LENGTH_SHORT).show()
                                                    }
                                                    .addOnFailureListener { e ->
                                                        Toast.makeText(this, "Error al guardar la solicitud en el especialista: ${e.message}", Toast.LENGTH_SHORT).show()
                                                    }
                                            }
                                            .addOnFailureListener { e ->
                                                Toast.makeText(this, "Error al guardar la solicitud en el cliente: ${e.message}", Toast.LENGTH_SHORT).show()
                                            }
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Error al obtener la descripción del servicio: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            } else {
                                Toast.makeText(this, "Especialista no encontrado", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error al obtener el especialista: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Cliente no encontrado", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al obtener el cliente: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

}
