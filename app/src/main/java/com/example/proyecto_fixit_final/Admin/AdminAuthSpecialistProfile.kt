package com.example.proyecto_fixit_final.Admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto_fixit_final.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class AdminAuthSpecialistProfile : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var specialistId: String
    private lateinit var imgPerfilEspecialista: ImageView
    private lateinit var btnAutorizarEspecialista: Button
    private lateinit var btnRechazarEspecialista: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_auth_specialist_profile)

        db = FirebaseFirestore.getInstance()

        specialistId = intent.getStringExtra("specialistId") ?: ""
        val nombre = intent.getStringExtra("nombre")
        val imageUrl = intent.getStringExtra("imageUrl")
        val rut = intent.getStringExtra("rut")
        val correo = intent.getStringExtra("correo")
        val ciudad = intent.getStringExtra("ciudad")
        val telefono = intent.getStringExtra("telefono")
        val profesion = intent.getStringExtra("profesion")

        // Configurar vistas
        val nombreTextView: TextView = findViewById(R.id.edNombre)
        val rutTextView: TextView = findViewById(R.id.edRut)
        val correoTextView: TextView = findViewById(R.id.edCorreo)
        val ciudadTextView: TextView = findViewById(R.id.edCiudad)
        val telefonoTextView: TextView = findViewById(R.id.edTelefono)
        val profesionTextView: TextView = findViewById(R.id.edEspecialidad)
        val btnAntecedentesEspecialista: Button = findViewById(R.id.btn_antecedentes_especialista) // Botón para descargar PDF

        nombreTextView.text = nombre
        rutTextView.text = rut
        correoTextView.text = correo
        ciudadTextView.text = ciudad
        telefonoTextView.text = telefono
        profesionTextView.text = profesion

        // Configurar acción para descargar y mostrar el archivo PDF
        btnAntecedentesEspecialista.setOnClickListener { descargarPdfEspecialista(specialistId) }

        imgPerfilEspecialista = findViewById(R.id.imgPerfilEspecialista)
        btnAutorizarEspecialista = findViewById(R.id.btnAutorizarEspecialista)
        btnRechazarEspecialista = findViewById(R.id.btnRechazarEspecialista)

        val titulo: TextView = findViewById(R.id.Autorizarespecialista)
        titulo.text = nombre

        if (!imageUrl.isNullOrEmpty()) {
            Picasso.get().load(imageUrl).into(imgPerfilEspecialista)
        } else {
            imgPerfilEspecialista.setImageResource(R.drawable.imagen_login)
        }

        btnAutorizarEspecialista.setOnClickListener {
            autorizarEspecialista()
        }

        btnRechazarEspecialista.setOnClickListener {
            mostrarDialogoConfirmacion()
        }

        // Configurar acción para volver a la lista de especialistas
        findViewById<ImageView>(R.id.flechavolver_Autorizarespecialista).setOnClickListener { irEspecialistasnoAuth() }
    }

    // Función para descargar y mostrar el archivo PDF del especialista
    private fun descargarPdfEspecialista(specialistId: String?) {
        if (!specialistId.isNullOrEmpty()) {
            val storage = FirebaseStorage.getInstance()
            val pdfRef = storage.getReference("pdfs/$specialistId.pdf")

            pdfRef.downloadUrl.addOnSuccessListener { uri ->
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = uri
                startActivity(intent)
                Toast.makeText(this, "Iniciando descarga de PDF", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { e ->
                Log.e("AuthSpecialistProfile", "Error al obtener URL del PDF: ${e.message}")
                Toast.makeText(this, "Error al obtener PDF", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "No se puede descargar el PDF", Toast.LENGTH_SHORT).show()
        }
    }

    private fun autorizarEspecialista() {
        db.collection("especialistas").document(specialistId)
            .update(mapOf(
                "autorizado" to true,
                "verified" to "si"
            ))
            .addOnSuccessListener {
                Toast.makeText(this, "Especialista autorizado", Toast.LENGTH_SHORT).show()
                limpiarYActualizarLista()
            }
            .addOnFailureListener { e ->
                Log.e("AuthSpecialistProfile", "Error al autorizar especialista: ${e.message}")
            }
    }

    private fun mostrarDialogoConfirmacion() {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Rechazo")
            .setMessage("¿Estás seguro de que deseas rechazar al especialista?")
            .setPositiveButton("Sí") { _, _ ->
                rechazarEspecialista()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun rechazarEspecialista() {
        db.collection("especialistas").document(specialistId)
            .update(mapOf(
                "verified" to "no",
                "motivoRechazo" to "Tu certificado de antecedentes no cumple con las normativas de Fixit. Ante cualquier duda, por favor, contáctanos en la sección de Ayuda."
            ))
            .addOnSuccessListener {
                Toast.makeText(this, "Especialista rechazado", Toast.LENGTH_SHORT).show()
                limpiarYActualizarLista()
            }
            .addOnFailureListener { e ->
                Log.e("AuthSpecialistProfile", "Error al rechazar especialista: ${e.message}")
            }
    }

    private fun limpiarYActualizarLista() {
        setResult(RESULT_OK)
        finish()
    }

    private fun irEspecialistasnoAuth() {
        val intent = Intent(this, AdminAuthSpecialist::class.java)
        startActivity(intent)
        finish()
    }
}
