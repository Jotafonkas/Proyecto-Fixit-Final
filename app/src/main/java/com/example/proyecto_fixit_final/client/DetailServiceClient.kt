package com.example.proyecto_fixit_final.Specialist

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.example.proyecto_fixit_final.Client.Request
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class DetailServiceClient : AppCompatActivity() {
    private lateinit var especialistaId: String
    private lateinit var nombreEspecialista: String
    private lateinit var nombreServicio: String
    private lateinit var servicioId: String
    private lateinit var clienteId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detalle_servicio_cliente)

        //obtencion de id de usuario
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        clienteId = currentUser.uid

        // Obtención de datos pasados por el intent
        especialistaId = intent.getStringExtra("uid") ?: ""
        nombreEspecialista = intent.getStringExtra("nombre") ?: ""
        nombreServicio = intent.getStringExtra("nombreServicio") ?: ""
        val imageUrl = intent.getStringExtra("imageUrl") ?: ""


        // Referencias a las vistas
        val imageView: ShapeableImageView = findViewById(R.id.imageServiceSpecialist)
        val nombreTextView: TextView = findViewById(R.id.input_nombre_especialista)
        val nombreServicioTextView: TextView = findViewById(R.id.input_nombre_servicio)
        val descripcionTextView: TextView = findViewById(R.id.input_descripcion_servicio)
        val requestServiceButton: Button = findViewById(R.id.botonsolicitar)

        // Asignación de datos a las vistas
        nombreTextView.text = nombreEspecialista
        nombreServicioTextView.text = nombreServicio
        Picasso.get().load(imageUrl).into(imageView)

        // Obtener la descripción y la imagen del servicio desde Firebase
        val db = FirebaseFirestore.getInstance()
        db.collection("especialistas").document(especialistaId)
            .collection("servicios").whereEqualTo("nombreServicio", nombreServicio)
            .get()
            .addOnSuccessListener { serviceDocuments ->
                for (serviceDocument in serviceDocuments) {
                    val descripcion = serviceDocument.getString("descripcionServicio") ?: ""
                    val precio = serviceDocument.getString("precio") ?: ""
                    val categoria = serviceDocument.getString("categoriaServicio") ?: ""
                    descripcionTextView.text = descripcion
                    servicioId = serviceDocument.id

                    Log.d("DetailServiceClient", "Descripcion obtenida: $descripcion, servicioId: $servicioId")

                    requestServiceButton.setOnClickListener {
                        if (servicioId.isEmpty()) {
                            Toast.makeText(this, "Espere a que se carguen los datos del servicio", Toast.LENGTH_SHORT).show()
                        } else {
                            requestService(descripcion, precio, imageUrl, categoria)
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                descripcionTextView.text = "Error al cargar la descripción: ${e.message}"
                Log.e("DetailServiceClient", "Error al cargar la descripción: ${e.message}", e)
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

    private fun requestService(descripcion: String, precio: String, imagenUrl: String, categoria: String) {
        val servicio = ServicioSolicitado(
            nombreEspecialista = nombreEspecialista,
            nombreServicio = nombreServicio,
            descripcionServicio = descripcion,
            imagenUrl = imagenUrl,
            precio = precio,
            uidservicio = servicioId
        )
        verificarYSolicitarServicio(servicio)
    }

    private fun verificarYSolicitarServicio(servicio: ServicioSolicitado) {
        val db = FirebaseFirestore.getInstance()
        db.collection("clientes").document(clienteId)
            .collection("SolicitudesPendientes").whereEqualTo("servicioId", servicio.uidservicio)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    registrarSolicitudPendiente(servicio)
                } else {
                    Toast.makeText(this, "El servicio ya ha sido solicitado", Toast.LENGTH_SHORT).show()
                    Log.d("DetailServiceClient", "El servicio con ID ${servicio.uidservicio} ya ha sido solicitado.")
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al verificar el servicio: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("DetailServiceClient", "Error al verificar el servicio: ${e.message}", e)
            }
    }

    private fun registrarSolicitudPendiente(servicio: ServicioSolicitado) {
        val db = FirebaseFirestore.getInstance()
        db.collection("clientes").document(clienteId).get()
            .addOnSuccessListener { clientDocument ->
                val nombreCliente = clientDocument.getString("nombre") ?: return@addOnSuccessListener
                val solicitudId = UUID.randomUUID().toString()
                val solicitud = Request(
                    id = solicitudId,
                    clienteId = clienteId,
                    servicioId = servicio.uidservicio,
                    estado = "pendiente",
                    nombreServicio = servicio.nombreServicio,
                    nombreCliente = nombreCliente,
                    nombreEspecialista = servicio.nombreEspecialista
                )

                db.collection("especialistas").document(especialistaId)
                    .collection("SolicitudesPendientes").document(solicitudId)
                    .set(solicitud)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Solicitud enviada para aprobación", Toast.LENGTH_SHORT).show()
                        Log.d("DetailServiceClient", "Solicitud pendiente enviada: $solicitud")
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al enviar la solicitud: ${e.message}", Toast.LENGTH_SHORT).show()
                        Log.e("DetailServiceClient", "Error al enviar la solicitud: ${e.message}", e)
                    }

                db.collection("clientes").document(clienteId)
                    .collection("SolicitudesPendientes").document(solicitudId)
                    .set(solicitud)
                    .addOnSuccessListener {
                        Log.d("DetailServiceClient", "Solicitud pendiente enviada al cliente: $solicitud")
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al enviar la solicitud al cliente: ${e.message}", Toast.LENGTH_SHORT).show()
                        Log.e("DetailServiceClient", "Error al enviar la solicitud al cliente: ${e.message}", e)
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al obtener el cliente: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("DetailServiceClient", "Error al obtener el cliente: ${e.message}", e)
            }
    }

    data class ServicioSolicitado(
        val nombreEspecialista: String,
        val descripcionServicio: String,
        val imagenUrl: String,
        val nombreServicio: String,
        val precio: String,
        val uidservicio: String
    )
}