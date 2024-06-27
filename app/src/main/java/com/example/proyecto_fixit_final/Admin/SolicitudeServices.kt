package com.example.proyecto_fixit_final.Admin

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.proyecto_fixit_final.R
import com.example.proyecto_fixit_final.Specialist.modelos.Servicio
import com.example.proyecto_fixit_final.utils.EmailSender
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.material.button.MaterialButton
import android.widget.Toast
import android.app.AlertDialog
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import android.util.Patterns
import com.google.firebase.auth.FirebaseAuth
import java.text.NumberFormat
import java.util.Locale

class SolicitudeServices : AppCompatActivity() {

    private lateinit var solicitudesContainer: LinearLayout
    private lateinit var db: FirebaseFirestore
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.solicitudes_admin)

        solicitudesContainer = findViewById(R.id.comentarios_container)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        swipeRefreshLayout.setOnRefreshListener {
            loadSolicitudes()
        }

        loadSolicitudes()
    }

    private fun loadSolicitudes() {
        solicitudesContainer.removeAllViews()

        db.collection("admin").document("solicitudes").collection("solicitud")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val servicio = document.toObject(Servicio::class.java)
                    val documentId = document.id
                    agregarSolicitud(servicio, documentId)
                }
                swipeRefreshLayout.isRefreshing = false
            }
            .addOnFailureListener { exception ->
                swipeRefreshLayout.isRefreshing = false
            }
    }

    @SuppressLint("SetTextI18n")
    private fun agregarSolicitud(servicio: Servicio, documentId: String) {
        val solicitudView = LayoutInflater.from(this).inflate(R.layout.cards_solicitudes_admin, solicitudesContainer, false)

        val imageSpecialistSolicitude: ImageView = solicitudView.findViewById(R.id.imageSpecialistSolicitude)
        val nameServiceSolicitude: TextView = solicitudView.findViewById(R.id.nameServiceSolicitude)
        val categorieServiceSolicitude: TextView = solicitudView.findViewById(R.id.categorieServiceSolicitude)
        val nameSpecialistSolicitude: TextView = solicitudView.findViewById(R.id.nameSpecialistSolicitude)
        val priceSpecialistSolicitude: TextView = solicitudView.findViewById(R.id.priceSpecialistSolicitude)
        val reviewButton: MaterialButton = solicitudView.findViewById(R.id.reviewButton)
        val rejectButton: MaterialButton = solicitudView.findViewById(R.id.rejectButton)

        nameServiceSolicitude.text = servicio.nombreServicio
        categorieServiceSolicitude.text = servicio.categoria

        // Formatear el precio con el signo de peso y puntos de miles
        val formattedPrice = NumberFormat.getCurrencyInstance(Locale("es", "CL")).format(servicio.precio.toDouble())
        priceSpecialistSolicitude.text = formattedPrice

        nameSpecialistSolicitude.text = servicio.nombreEspecialista
        Glide.with(this).load(servicio.imagenUrl).into(imageSpecialistSolicitude)

        reviewButton.setOnClickListener {
            mostrarDialogoConfirmacionParaAceptar(servicio, documentId, solicitudView)
        }

        rejectButton.setOnClickListener {
            mostrarDialogoConfirmacionParaRechazar(solicitudView, documentId, servicio.uid)
        }

        solicitudesContainer.addView(solicitudView)
    }

    private fun mostrarDialogoConfirmacionParaAceptar(servicio: Servicio, documentId: String, solicitudView: View) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Aceptar Solicitud")
        builder.setMessage("¿Está seguro que desea aceptar esta solicitud?")

        builder.setPositiveButton("Sí") { dialog, _ ->
            aceptarSolicitud(servicio, documentId, solicitudView)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun aceptarSolicitud(servicio: Servicio, documentId: String, solicitudView: View) {
        db.collection("admin").document("solicitudes").collection("solicitud").document(documentId)
            .update("estado", "aceptada")
            .addOnSuccessListener {
                enviarCorreo(servicio.uid, servicio.nombreServicio)
                db.collection("admin").document("solicitudes").collection("solicitud").document(documentId)
                    .delete()
                    .addOnSuccessListener {
                        solicitudesContainer.removeView(solicitudView)
                        Toast.makeText(this, "Solicitud aceptada y eliminada.", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al eliminar la solicitud: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al aceptar la solicitud: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun enviarCorreo(uid: String, nombreServicio: String) {
        db.collection("especialistas").document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val email = document.getString("correo")
                    if (!email.isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        val subject = "Solicitud de Servicio Aceptada"
                        val body = "Su solicitud para el servicio $nombreServicio ha sido aceptada."
                        val apiKey = "your_sendgrid_api_key"
                        println("Enviando correo a $email")  // Agregar registro de depuración
                        EmailSender(apiKey, email, subject, body).execute()
                        println("Correo enviado a $email")  // Agregar registro de depuración
                        Toast.makeText(this, "Correo de aceptación enviado a $email.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "El correo electrónico no es válido.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "No se pudo encontrar el especialista.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al obtener el correo del especialista: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun mostrarDialogoConfirmacionParaRechazar(view: View, documentId: String, uid: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Rechazar Solicitud")
        builder.setMessage("¿Está seguro que desea rechazar esta solicitud?")

        builder.setPositiveButton("Sí") { dialog, _ ->
            eliminarSolicitudYServicio(view, documentId, uid)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun eliminarSolicitudYServicio(view: View, documentId: String, uid: String) {
        db.collection("admin").document("solicitudes").collection("solicitud").document(documentId)
            .delete()
            .addOnSuccessListener {
                db.collection("especialistas").document(uid).collection("servicios").document(documentId)
                    .delete()
                    .addOnSuccessListener {
                        solicitudesContainer.removeView(view)
                        Toast.makeText(this, "Solicitud rechazada y servicio eliminado exitosamente.", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al eliminar el servicio: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al eliminar la solicitud: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onResume() {
        super.onResume()
        loadSolicitudes()
    }

    fun backMenu(view: View) {
        super.onBackPressed()
    }
}
