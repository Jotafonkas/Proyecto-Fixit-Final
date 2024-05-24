package com.example.proyecto_fixit_final.Specialist

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto_fixit_final.databinding.CrearServicioEspecialistaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

// Clase para crear un servicio especialista
class CreateServicesSpecialist : AppCompatActivity() {

    private lateinit var binding: CrearServicioEspecialistaBinding
    private var imageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var uid: String

    // Función para crear la vista de la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Crear la vista de la actividad
        binding = CrearServicioEspecialistaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtener el nombre del servicio seleccionado y el UID del usuario
        val serviceName = intent.getStringExtra("serviceName")
        uid = intent.getStringExtra("uid") ?: ""
        binding.edCategoriaServicio.text = serviceName

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
        val categoria = binding.edCategoriaServicio.text.toString().trim()

        // Validar que los campos no estén vacíos
        if (nombreServicio.isEmpty()) {
            binding.edNombreServicio.error = "Por favor, complete este campo."
            return
        }

        if (descripcionServicio.isEmpty()) {
            binding.edDescripcionServicio.error = "Por favor, complete este campo."
            return
        }

        if (precio.isEmpty()) {
            binding.edPrecio.error = "Por favor, complete este campo."
            return
        }

        if (imageUri == null) {
            Toast.makeText(this, "Por favor, seleccione al menos una imagen.", Toast.LENGTH_SHORT).show()
            return
        }

        if (categoria.isEmpty()) {
            binding.edCategoriaServicio.error = "Por favor, complete este campo."
            return
        }

        // Validar que el nombre del servicio solo contenga letras
        if (!nombreServicio.matches(Regex("^[a-zA-Z ]+$"))) {
            binding.edNombreServicio.error = "El nombre del servicio solo puede contener letras."
            return
        }

        // Validar que la descripción no exceda los 150 caracteres
        if (descripcionServicio.length > 150) {
            binding.edDescripcionServicio.error = "La descripción no puede exceder los 150 caracteres."
            return
        }

        // Validar que el precio solo contenga números
        if (precio.toDoubleOrNull() == null) {
            binding.edPrecio.error = "El precio solo puede contener números."
            return
        }

        // Validar que se haya seleccionado al menos una imagen
        if (imageUri == null) {
            Toast.makeText(this, "Por favor, seleccione al menos una imagen.", Toast.LENGTH_SHORT).show()
            return
        }

        // Subir la imagen a Firebase Storage
        val storageReference = FirebaseStorage.getInstance().reference
        val fileReference = storageReference.child("servicios/${uid}/${UUID.randomUUID()}.jpg")

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
                        "categoria" to categoria,
                        "imagenUrl" to downloadUrl,
                        "uid" to uid
                    )
                    // Guardar el servicio en Firestore bajo la colección del usuario actual
                    db.collection("users").document(uid).collection("servicios")
                        .add(servicio)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Servicio publicado exitosamente.", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, ViewSpecialistServices::class.java)
                            intent.putExtra("uid", uid) // Pasar el UID a la siguiente actividad
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
