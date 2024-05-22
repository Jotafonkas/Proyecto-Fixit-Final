package com.example.proyecto_fixit_final.Specialist

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto_fixit_final.R
import com.example.proyecto_fixit_final.SelectUser
import com.example.proyecto_fixit_final.fragments.HomeFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class RegisterSpecialist : AppCompatActivity() {

    // Declarar las variables del layout
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var btnRegistro: Button
    private lateinit var btnFoto: Button
    private lateinit var btnEliminar: Button
    private lateinit var btnCertificado: Button
    private lateinit var imagen: ImageView
    private lateinit var nombreEsp: EditText
    private lateinit var rutEsp: EditText
    private lateinit var correoEsp: EditText
    private lateinit var telefonoEsp: EditText
    private lateinit var profesionEsp: EditText
    private lateinit var passEsp: EditText
    private lateinit var pass2Esp: EditText
    private var selectedImageUri: Uri? = null
    private var selectedPdfUri: Uri? = null


    fun returnRegisterUser(view: View) {
        val intent = Intent(this, SelectUser::class.java)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registro_especialista)
        auth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()
        storage = Firebase.storage

        // Set variables por id
        btnRegistro = findViewById(R.id.btnRegistrarse_especialista)
        btnFoto = findViewById(R.id.btnFoto)
        btnEliminar = findViewById(R.id.btnEliminar)
        btnCertificado = findViewById(R.id.btnCargarCertificado)
        imagen = findViewById(R.id.imagen)
        nombreEsp = findViewById(R.id.edNombre_especialista)
        rutEsp = findViewById(R.id.edRut_especialista)
        correoEsp = findViewById(R.id.edCorreo_especialista)
        telefonoEsp = findViewById(R.id.edTelefono_especialista)
        profesionEsp = findViewById(R.id.edEspecialidad)
        passEsp = findViewById(R.id.edContraseña_especialista)
        pass2Esp = findViewById(R.id.edConfirmaContraseña_especialista)

        btnRegistro.setOnClickListener {
            // Obtener datos del registro
            val nombre: String = nombreEsp.text.toString()
            val rut: String = rutEsp.text.toString()
            val correo: String = correoEsp.text.toString()
            val telefono: String = telefonoEsp.text.toString()
            val profesion: String = profesionEsp.text.toString()
            val pass: String = passEsp.text.toString()
            val pass2: String = pass2Esp.text.toString()

            // Validaciones
            if (nombre.isEmpty()) {
                nombreEsp.error = "Ingrese su nombre"
                return@setOnClickListener
            }
            if (rut.isEmpty()) {
                rutEsp.error = "Ingrese su rut"
                return@setOnClickListener
            }
            if (correo.isEmpty()) {
                correoEsp.error = "Ingrese su correo"
                return@setOnClickListener
            }
            if (telefono.isEmpty()) {
                telefonoEsp.error = "Ingrese su telefono"
                return@setOnClickListener
            }
            if (profesion.isEmpty()) {
                profesionEsp.error = "Ingrese su profesion/especialidad"
                return@setOnClickListener
            }
            if (pass.isEmpty()) {
                passEsp.error = "Ingrese su contraseña"
                return@setOnClickListener
            }
            if (pass2.isEmpty()) {
                pass2Esp.error = "Repita su contraseña"
                return@setOnClickListener
            }
            if (pass == pass2) {
                registrarNuevoUsuario(nombre, rut, correo, telefono, profesion, pass)
            } else {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show()
            }
        }

        btnFoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE)
        }

        btnEliminar.setOnClickListener {
            imagen.setImageResource(R.drawable.image_perfil) // Cambia esto por el recurso predeterminado que deseas
            selectedImageUri = null
        }

        btnCertificado.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "application/pdf"
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            startActivityForResult(Intent.createChooser(intent, "Seleccionar PDF"), REQUEST_CODE_SELECT_PDF)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_SELECT_IMAGE -> {
                    data?.data?.let { uri ->
                        selectedImageUri = uri
                        imagen.setImageURI(uri)
                    }
                }
                REQUEST_CODE_SELECT_PDF -> {
                    data?.data?.let { uri ->
                        selectedPdfUri = uri
                        Toast.makeText(this, "PDF seleccionado: $uri", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    companion object {
        private const val REQUEST_CODE_SELECT_IMAGE = 1000
        private const val REQUEST_CODE_SELECT_PDF = 1001
    }

    private fun registrarNuevoUsuario(nombre: String, rut: String, correo: String, telefono: String, profesion: String, pass: String) {
        auth.createUserWithEmailAndPassword(correo, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //valida el usuario y usa la funcion savedata
                    val user = auth.currentUser
                    if (user != null) {
                        if (selectedImageUri != null) {
                            uploadImageToStorage(user.uid, nombre, rut, correo, telefono, profesion, null)
                        } else if (selectedPdfUri != null) {
                            uploadPdfToStorage(user.uid, nombre, rut, correo, telefono, profesion, null)
                        } else {
                            saveAdditionalUserData(user.uid, nombre, rut, correo, telefono, profesion, null, null)
                            navigateToHome()
                        }
                    }
                } else {
                    // Error al crear usuario
                    Toast.makeText(this, "Error al crear usuario: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun uploadImageToStorage(uid: String, nombre: String, rut: String, correo: String, telefono: String, profesion: String, pdfUrl: String?) {
        val ref = storage.reference.child("images/$uid.jpg")
        val uploadTask = ref.putFile(selectedImageUri!!)

        uploadTask.addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener { uri ->
                if (selectedPdfUri != null) {
                    uploadPdfToStorage(uid, nombre, rut, correo, telefono, profesion, uri.toString())
                } else {
                    saveAdditionalUserData(uid, nombre, rut, correo, telefono, profesion, uri.toString(), pdfUrl)
                    navigateToHome()
                }
            }
        }.addOnFailureListener { e ->
            Log.e("RegisterSpecialist", "Error al subir imagen", e)
            Toast.makeText(this, "Error al subir imagen: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }


    private fun uploadPdfToStorage(uid: String, nombre: String, rut: String, correo: String, telefono: String, profesion: String, imageUrl: String?) {
        val ref = storage.reference.child("pdfs/$uid.pdf")
        val uploadTask = ref.putFile(selectedPdfUri!!)

        uploadTask.addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener { uri ->
                saveAdditionalUserData(uid, nombre, rut, correo, telefono, profesion, imageUrl, uri.toString())
                navigateToHome()
            }
        }.addOnFailureListener { e ->
            Log.e("RegisterSpecialist", "Error al subir PDF", e)
            Toast.makeText(this, "Error al subir PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    private fun saveAdditionalUserData(uid: String, nombre: String, rut: String, correo: String, telefono: String, profesion: String, imageUrl: String?, pdfUrl: String?) {
        val user = hashMapOf(
            "nombre" to nombre,
            "rut" to rut,
            "correo" to correo,
            "telefono" to telefono,
            "profesion" to profesion,
            "imageUrl" to imageUrl,
            "pdfUrl" to pdfUrl
        )
        firestore.collection("users").document(uid)
            .set(user)
            .addOnSuccessListener {
                Log.d("RegisterSpecialist", "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { e ->
                Log.w("RegisterSpecialist", "Error writing document", e)
            }
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeFragment::class.java)
        startActivity(intent)
        finish()
    }

}
