package com.example.proyecto_fixit_final.Admin

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.proyecto_fixit_final.R
import com.example.proyecto_fixit_final.Specialist.modelos.Services
import com.example.proyecto_fixit_final.utils.AdminEmailSender
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Locale

class AdminSolicitudeServices : AppCompatActivity() {

    private lateinit var solicitudesContainer: LinearLayout
    private lateinit var db: FirebaseFirestore
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_solicitudes)

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
                solicitudesContainer.removeAllViews()
                for (document in result) {
                    val servicio = document.toObject(Services::class.java)
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
    private fun agregarSolicitud(servicio: Services, documentId: String) {
        val solicitudView = LayoutInflater.from(this).inflate(R.layout.admin_card_solicitudes, solicitudesContainer, false)

        val imageSpecialistSolicitude: ImageView = solicitudView.findViewById(R.id.imageSpecialistSolicitude)
        val nameServiceSolicitude: TextView = solicitudView.findViewById(R.id.nameServiceSolicitude)
        val categorieServiceSolicitude: TextView = solicitudView.findViewById(R.id.categorieServiceSolicitude)
        val nameSpecialistSolicitude: TextView = solicitudView.findViewById(R.id.nameSpecialistSolicitude)
        val priceSpecialistSolicitude: TextView = solicitudView.findViewById(R.id.priceSpecialistSolicitude)
        val reviewButton: MaterialButton = solicitudView.findViewById(R.id.reviewButton)
        val rejectButton: MaterialButton = solicitudView.findViewById(R.id.rejectButton)

        nameServiceSolicitude.text = servicio.nombreServicio
        categorieServiceSolicitude.text = servicio.categoria
        nameSpecialistSolicitude.text = servicio.nombreEspecialista

        val precioString = servicio.precio
        if (precioString.isNullOrEmpty()) {
            priceSpecialistSolicitude.text = "Precio no disponible"
        } else {
            try {
                val formattedPrice = NumberFormat.getCurrencyInstance(Locale("es", "CL")).format(precioString.toDouble())
                priceSpecialistSolicitude.text = formattedPrice
            } catch (e: NumberFormatException) {
                priceSpecialistSolicitude.text = "Precio no válido"
            }
        }

        Glide.with(this).load(servicio.imagenUrl).into(imageSpecialistSolicitude)

        reviewButton.setOnClickListener {
            mostrarDialogoConfirmacionParaAceptar(servicio, documentId, solicitudView)
        }

        rejectButton.setOnClickListener {
            mostrarDialogoConfirmacionParaRechazar(solicitudView, documentId, servicio.uid)
        }

        solicitudesContainer.addView(solicitudView)
    }

    private fun mostrarDialogoConfirmacionParaAceptar(servicio: Services, documentId: String, solicitudView: View) {
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

    private fun aceptarSolicitud(servicio: Services, documentId: String, solicitudView: View) {
        val especialistaDocRef = db.collection("especialistas").document(servicio.uid).collection("servicios").document(documentId)

        db.runTransaction { transaction ->
            transaction.update(especialistaDocRef, "estado", "Verificado")
            transaction.delete(db.collection("admin").document("solicitudes").collection("solicitud").document(documentId))
        }.addOnSuccessListener {
            solicitudesContainer.removeView(solicitudView)
            enviarCorreo(servicio.uid, servicio.nombreServicio, true)
            Toast.makeText(this, "Solicitud aceptada y servicio actualizado.", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Error al aceptar la solicitud: ${e.message}", Toast.LENGTH_SHORT).show()
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
                        enviarCorreo(uid, "", false)
                        // Enviar un broadcast para notificar que el servicio ha sido eliminado
                        val intent = Intent("com.example.proyecto_fixit_final.SERVICIO_ELIMINADO")
                        intent.putExtra("documentId", documentId)
                        sendBroadcast(intent)
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

    private fun enviarCorreo(uid: String, nombreServicio: String, aceptada: Boolean) {
        db.collection("especialistas").document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val email = document.getString("correo")
                    if (!email.isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        val subject = if (aceptada) "Solicitud de Servicio Aceptada" else "Solicitud de Servicio Rechazada"
                        val body = if (aceptada) {
                            "Su solicitud para el servicio $nombreServicio ha sido aceptada."
                        } else {
                            "Su solicitud del servicio ha sido rechazada por no cumplir con los requisitos necesarios para ser verificada." +
                                    "\n\nPor favor, póngase en contacto con el administrador para más información." +
                                    "\n\nAtentamente,\nAdministración FixIt."
                        }
                        sendEmail(email, subject, body)
                        Toast.makeText(this, "Correo de ${if (aceptada) "aceptación" else "rechazo"} enviado a $email.", Toast.LENGTH_SHORT).show()
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

    private fun sendEmail(email: String, subject: String, body: String) {
        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }

        try {
            startActivity(Intent.createChooser(emailIntent, "Enviar correo utilizando..."))
        } catch (ex: android.content.ActivityNotFoundException) {
            Toast.makeText(this, "No hay clientes de correo instalados.", Toast.LENGTH_SHORT).show()
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
