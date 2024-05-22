package com.example.proyecto_fixit_final.Specialist

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto_fixit_final.databinding.CrearServicioEspecialistaBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

// Clase para crear un servicio especialista
class CreateServicesSpecialist : AppCompatActivity() {

    private lateinit var binding: CrearServicioEspecialistaBinding
    private var imageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1

    // Función para crear la vista de la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Crear la vista de la actividad
        binding = CrearServicioEspecialistaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Botón para cargar imágenes
        binding.btnCargarImagenes.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
        }

        // Botón para publicar el servicio
        binding.btnPublicar.setOnClickListener {
            publicarServicio()
        }

        // Botón para volver a la actividad anterior
        binding.imgbtnVolver.setOnClickListener {
            finish() // Volver a la actividad anterior
        }
    }

    // Función para obtener la imagen seleccionada
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Obtener la imagen seleccionada
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
        }
    }

    // Función para publicar el servicio
    private fun publicarServicio() {
        val nombreServicio = binding.edNombreServicio.text.toString().trim()
        val descripcionServicio = binding.edDescripcionServicio.text.toString().trim()
        val precio = binding.edPrecio.text.toString().trim()

        // Validar que los campos no estén vacíos
        if (nombreServicio.isEmpty() || descripcionServicio.isEmpty() || precio.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Por favor, complete todos los campos y cargue una imagen.", Toast.LENGTH_SHORT).show()
            return
        }

        // Subir la imagen a Firebase Storage
        val storageReference = FirebaseStorage.getInstance().reference
        val fileReference = storageReference.child("servicios/${UUID.randomUUID()}.jpg")

        // Subir la imagen a Firebase Storage
        fileReference.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    val db = FirebaseFirestore.getInstance()
                    val servicio = hashMapOf(
                        "nombreServicio" to nombreServicio,
                        "descripcionServicio" to descripcionServicio,
                        "precio" to precio,
                        "imagenUrl" to downloadUrl
                    )
                    // Guardar el servicio en Firestore
                    db.collection("servicios")
                        .add(servicio)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Servicio publicado exitosamente.", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, ViewSpecialistServices::class.java)
                            startActivity(intent)
                            finish()
                        }
                        // Mostrar mensaje de error si no se pudo publicar el servicio
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error al publicar el servicio: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            // Mostrar mensaje de error si no se pudo cargar la imagen
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar la imagen: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}