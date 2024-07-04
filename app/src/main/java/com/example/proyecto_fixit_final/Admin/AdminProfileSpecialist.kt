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

class AdminProfileSpecialist : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var imageUrl: String // Variable para almacenar la URL de la imagen actual

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_perfil_especialista)

        // Recibir datos del Intent
        val nombre = intent.getStringExtra("nombre")
        imageUrl = intent.getStringExtra("imageUrl") ?: ""
        val rut = intent.getStringExtra("rut")
        val correo = intent.getStringExtra("correo")
        val ciudad = intent.getStringExtra("ciudad")
        val telefono = intent.getStringExtra("telefono")
        val specialistId = intent.getStringExtra("specialistId") // Obtener el ID del especialista
        val profesion = intent.getStringExtra("profesion")

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance()

        // Configurar vistas
        val nombreTextView: TextView = findViewById(R.id.edNombre)
        val imagenImageView: ImageView = findViewById(R.id.imgPerfilEspecialista)
        val rutTextView: TextView = findViewById(R.id.edRut)
        val correoTextView: TextView = findViewById(R.id.edCorreo)
        val ciudadTextView: TextView = findViewById(R.id.edCiudad)
        val telefonoTextView: TextView = findViewById(R.id.edTelefono)
        val profesionTextView: TextView = findViewById(R.id.edEspecialidad)
        val btnEliminar: Button = findViewById(R.id.btnEliminar)
        val btnEliminarPerfilEspecialista: Button = findViewById(R.id.btnEliminarPerfilEspecialista)
        val btnAntecedentesEspecialista: Button = findViewById(R.id.btn_antecedentes_especialista) // Botón para descargar PDF
        val btnServiciosEspecialista: Button = findViewById(R.id.btn_servicios_especialista) // Botón para ver servicios

        nombreTextView.text = nombre
        rutTextView.text = rut
        correoTextView.text = correo
        ciudadTextView.text = ciudad
        telefonoTextView.text = telefono
        profesionTextView.text = profesion

        if (!imageUrl.isNullOrEmpty()) {
            Picasso.get().load(imageUrl).into(imagenImageView)
        } else {
            imagenImageView.setImageResource(R.drawable.imagen_login) // Imagen predeterminada
        }

        // Configurar botones para volver al sitio indicado
        val flechaVolver: ImageView = findViewById(R.id.flechavolver_listaespecialistas)
        val menu: TextView = findViewById(R.id.menu)

        flechaVolver.setOnClickListener { irAListaEspecialistas() }
        menu.setOnClickListener { irAlMenu() }

        // Configurar acción de eliminar foto con confirmación
        btnEliminar.setOnClickListener { mostrarConfirmacionEliminarFoto() }

        // Configurar acción de eliminar perfil de especialista con confirmación
        btnEliminarPerfilEspecialista.setOnClickListener { mostrarConfirmacionEliminarEspecialista(specialistId) }

        // Configurar acción para descargar y mostrar el archivo PDF
        btnAntecedentesEspecialista.setOnClickListener { descargarPdfEspecialista(specialistId) }

        // Configurar acción para ver servicios del especialista
        btnServiciosEspecialista.setOnClickListener { verServiciosEspecialista(specialistId) }
    }

    // Función para volver a la lista de especialistas
    private fun irAListaEspecialistas() {
        val intent = Intent(this, AdminListSpecialists::class.java)
        startActivity(intent)
        finish()
    }

    // Función para volver al menú
    private fun irAlMenu() {
        val intent = Intent(this, AdminMenu::class.java)
        startActivity(intent)
        finish()
    }

    // Función para mostrar la confirmación de eliminación de perfil de especialista
    private fun mostrarConfirmacionEliminarEspecialista(specialistId: String?) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmación")
        builder.setMessage("¿Está seguro de eliminar este usuario?")

        builder.setPositiveButton("Eliminar") { dialog, which ->
            eliminarPerfilEspecialista(specialistId)
        }

        builder.setNegativeButton("Cancelar") { dialog, which ->
            dialog.dismiss()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    // Función para eliminar el perfil del especialista
    private fun eliminarPerfilEspecialista(specialistId: String?) {
        if (!specialistId.isNullOrEmpty()) {
            db.collection("especialistas").document(specialistId)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Perfil de especialista eliminado correctamente", Toast.LENGTH_SHORT).show()
                    irAListaEspecialistas()
                }
                .addOnFailureListener { e ->
                    Log.e("ProfileSpecialistAdmin", "Error al eliminar perfil del especialista: ${e.message}")
                    Toast.makeText(this, "Error al eliminar perfil del especialista", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "No se puede eliminar el perfil del especialista", Toast.LENGTH_SHORT).show()
        }
    }

    // Función para mostrar la confirmación de eliminación de foto
    private fun mostrarConfirmacionEliminarFoto() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmación")
        builder.setMessage("¿Está seguro de eliminar la foto del usuario?")

        builder.setPositiveButton("Eliminar") { dialog, which ->
            eliminarFotoEspecialista()
        }

        builder.setNegativeButton("Cancelar") { dialog, which ->
            dialog.dismiss()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    // Función para eliminar la foto del especialista
    private fun eliminarFotoEspecialista() {
        if (!imageUrl.isNullOrEmpty()) {
            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.getReferenceFromUrl(imageUrl)

            storageRef.delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Imagen eliminada correctamente", Toast.LENGTH_SHORT).show()
                    val imagenImageView: ImageView = findViewById(R.id.imgPerfilEspecialista)
                    imagenImageView.setImageResource(R.drawable.imagen_login) // Imagen predeterminada

                    // Actualizar la URL de la imagen en Firestore
                    val specialistId = intent.getStringExtra("specialistId")
                    if (!specialistId.isNullOrEmpty()) {
                        val especialistaRef = db.collection("especialistas").document(specialistId)
                        especialistaRef.update("imageUrl", null)
                            .addOnSuccessListener {
                                Log.d("ProfileSpecialistAdmin", "URL de imagen actualizada en Firestore")
                                imageUrl = "" // Limpiar la URL de la imagen localmente
                            }
                            .addOnFailureListener { e ->
                                Log.e("ProfileSpecialistAdmin", "Error al actualizar URL de imagen: ${e.message}")
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("ProfileSpecialistAdmin", "Error al eliminar imagen: ${e.message}")
                    Toast.makeText(this, "Error al eliminar imagen", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "No se puede eliminar la imagen", Toast.LENGTH_SHORT).show()
        }
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
                Toast.makeText(this, "Iniciando descarga de PDF", Toast.LENGTH_SHORT).show() // Mostrar mensaje después de iniciar la Intent
            }.addOnFailureListener { e ->
                Log.e("ProfileSpecialistAdmin", "Error al obtener URL del PDF: ${e.message}")
                Toast.makeText(this, "Error al obtener PDF", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "No se puede descargar el PDF", Toast.LENGTH_SHORT).show()
        }
    }

    // Función para ver los servicios del especialista
    private fun verServiciosEspecialista(specialistId: String?) {
        if (!specialistId.isNullOrEmpty()) {
            // Crear un Intent para abrir la actividad de servicios del especialista
            val intent = Intent(this, AdminSpecialistServices::class.java)

            // Pasar el ID del especialista como extra en el Intent
            intent.putExtra("specialistId", specialistId)

            // Iniciar la actividad para ver los servicios del especialista
            startActivity(intent)
        } else {
            Toast.makeText(this, "No se puede obtener los servicios", Toast.LENGTH_SHORT).show()
        }
    }
}
