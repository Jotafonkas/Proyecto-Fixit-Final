package com.example.proyecto_fixit_final.Specialist

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto_fixit_final.R
import com.example.proyecto_fixit_final.databinding.CrearServicioEspecialistaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class CreateServicesSpecialist : AppCompatActivity() {

    private lateinit var binding: CrearServicioEspecialistaBinding
    private var imageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var uid: String
    private lateinit var nombreEspecialista: String
    private lateinit var loaderDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupLoader()

        binding = CrearServicioEspecialistaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val serviceName = intent.getStringExtra("serviceName")
        uid = intent.getStringExtra("uid") ?: ""
        binding.edCategoriaServicio.text = serviceName

        obtenerNombreEspecialista(uid)

        binding.btnCargarImagenes.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
        }

        binding.btnPublicar.setOnClickListener {
            publicarServicio()
        }

        binding.imgbtnVolver.setOnClickListener {
            finish()
        }
    }

    private fun setupLoader() {
        loaderDialog = Dialog(this)
        loaderDialog.setContentView(R.layout.loader_registro)
        loaderDialog.setCancelable(false)
        loaderDialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        loaderDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun showLoader() {
        if (!loaderDialog.isShowing) {
            loaderDialog.show()
        }
    }

    private fun hideLoader() {
        if (loaderDialog.isShowing) {
            loaderDialog.dismiss()
        }
    }

    private fun obtenerNombreEspecialista(uid: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("especialistas").document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    nombreEspecialista = document.getString("nombre") ?: "Especialista"
                }
            }
            .addOnFailureListener {
                nombreEspecialista = "Especialista"
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
        }
    }

    private fun publicarServicio() {
        val nombreServicio = binding.edNombreServicio.text.toString().trim()
        val descripcionServicio = binding.edDescripcionServicio.text.toString().trim()
        val precio = binding.edPrecio.text.toString().trim()
        val categoria = binding.edCategoriaServicio.text.toString().trim()

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

        if (!nombreServicio.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ ]+$"))) {
            binding.edNombreServicio.error = "El nombre del servicio solo puede contener letras."
            return
        }

        if (descripcionServicio.length > 150) {
            binding.edDescripcionServicio.error = "La descripción no puede exceder los 150 caracteres."
            return
        }

        if (precio.toDoubleOrNull() == null) {
            binding.edPrecio.error = "El precio solo puede contener números."
            return
        }

        showLoader()

        val storageReference = FirebaseStorage.getInstance().reference
        val fileReference = storageReference.child("servicios/${uid}/${UUID.randomUUID()}.jpg")

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
                        "uid" to uid,
                        "nombreEspecialista" to nombreEspecialista,
                        "estado" to "Pendiente"
                    )

                    db.collection("especialistas").document(uid).collection("servicios")
                        .add(servicio)
                        .addOnSuccessListener { documentReference ->
                            val serviceId = documentReference.id

                            db.collection("admin").document("solicitudes").collection("solicitud").document(serviceId).set(servicio)
                                .addOnSuccessListener {
                                    hideLoader()
                                    Toast.makeText(this, "Servicio publicado exitosamente.", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, ViewSpecialistServices::class.java)
                                    intent.putExtra("uid", uid)
                                    startActivity(intent)
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    hideLoader()
                                    Toast.makeText(this, "Error al publicar la solicitud: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                        .addOnFailureListener { e ->
                            hideLoader()
                            Toast.makeText(this, "Error al publicar el servicio: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                hideLoader()
                Toast.makeText(this, "Error al cargar la imagen: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    fun backMenu(view: View) {
        super.onBackPressed()
    }
}
