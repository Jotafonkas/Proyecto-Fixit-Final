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
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto_fixit_final.Login
import com.example.proyecto_fixit_final.R
import com.example.proyecto_fixit_final.SelectUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

@Suppress("DEPRECATION")
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
    private lateinit var loader: ProgressBar
    private var selectedImageUri: Uri? = null
    private var selectedPdfUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registro_especialista)
        auth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()
        storage = Firebase.storage

        // Declarar las variables del layout
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
        loader = findViewById(R.id.loader)

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
            if (nombre.isEmpty() || !nombre.matches(Regex("^[a-zA-Z ]+$"))) {
                nombreEsp.error = "Ingrese su nombre (solo letras)"
                return@setOnClickListener
            }

            if (rut.isEmpty() || rut.length > 9 || rut.toDoubleOrNull() == null) {
                rutEsp.error = "Ingrese su rut (solo números)"
                return@setOnClickListener
            }

            if (correo.isEmpty()) {
                correoEsp.error = "Ingrese su correo"
                return@setOnClickListener
            }

            if (telefono.isEmpty() || telefono.toDoubleOrNull() == null || telefono.length != 9) {
                telefonoEsp.error = "Ingrese su telefono (9 números)"
                return@setOnClickListener
            }

            if (profesion.isEmpty() || !profesion.matches(Regex("^[a-zA-Z ]+$"))) {
                profesionEsp.error = "Ingrese su profesion/especialidad (solo letras)"
                return@setOnClickListener
            }

            var isPasswordValid = true

            if (pass.isEmpty() || !pass.matches(Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=.,])(?=\\S+$).{6,}$"))) {
                passEsp.error = "Ingrese su contraseña (al menos 6 caracteres, 1 número, 1 letra minúscula, 1 letra mayúscula y 1 carácter especial)"
                isPasswordValid = false
            }

            if (pass2.isEmpty() || !pass2.matches(Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=.,])(?=\\S+$).{6,}$"))) {
                pass2Esp.error = "Repita su contraseña (al menos 6 caracteres, 1 número, 1 letra minúscula, 1 letra mayúscula y 1 carácter especial)"
                isPasswordValid = false
            }

            if (!isPasswordValid) {
                return@setOnClickListener
            }

            if (selectedImageUri == null) {
                Toast.makeText(this, "Por favor, suba su imagen de perfil.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (selectedPdfUri == null) {
                Toast.makeText(this, "Por favor, suba su certificado de antecedentes.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            validarRut(rut) { isRutValido ->
                if (isRutValido) {
                    // El RUT es válido, proceder con el registro

                    if (pass == pass2) {
                        loader.visibility = View.VISIBLE
                        registrarNuevoUsuario(nombre, rut, correo, telefono, profesion, pass)
                    } else {
                        pass2Esp.error = "Las contraseñas no coinciden"
                        Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show()
                    }
                } else {
                    rutEsp.error = "Este RUT ya está registrado"
                    Toast.makeText(this, "Este RUT ya está registrado", Toast.LENGTH_LONG).show()
                }
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

    // Método llamado al recibir resultados de actividades externas
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

    // Método para validar si el RUT está repetido
    private fun validarRut(rut: String, callback: (Boolean) -> Unit) {
        firestore.collection("especialistas")
            .whereEqualTo("rut", rut)
            .get()
            .addOnSuccessListener { documents ->
                callback(documents.isEmpty)
            }
            .addOnFailureListener { exception ->
                Log.e("RegisterSpecialist", "Error al validar el RUT: ${exception.message}", exception)
                // Manejar el error apropiadamente
                callback(false)
            }
    }

    // Método para registrar un nuevo usuario en Firebase Auth
    private fun registrarNuevoUsuario(nombre: String, rut: String, correo: String, telefono: String, profesion: String, pass: String) {
        auth.createUserWithEmailAndPassword(correo, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.sendEmailVerification()?.addOnCompleteListener { verificationTask ->
                        if (verificationTask.isSuccessful) {
                            Toast.makeText(this, "Correo de verificación enviado a ${user.email}", Toast.LENGTH_LONG).show()
                            user.let {
                                if (selectedImageUri != null) {
                                    uploadImageToStorage(it.uid, nombre, rut, correo, telefono, profesion)
                                }
                            }
                        } else {
                            Toast.makeText(this, "Error al enviar el correo de verificación: ${verificationTask.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Error al crear usuario: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    // Método para subir la imagen al almacenamiento de Firebase
    private fun uploadImageToStorage(uid: String, nombre: String, rut: String, correo: String, telefono: String, profesion: String) {
        val ref = storage.reference.child("images/$uid.jpg")
        val uploadTask = ref.putFile(selectedImageUri!!)

        uploadTask.addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener { uri ->
                if (selectedPdfUri != null) {
                    uploadPdfToStorage(uid, nombre, rut, correo, telefono, profesion, uri.toString())
                } else {
                    saveAdditionalUserData(uid, nombre, rut, correo, telefono, profesion, uri.toString(), null)
                }
            }
        }.addOnFailureListener { e ->
            Log.e("RegisterSpecialist", "Error al subir imagen", e)
            Toast.makeText(this, "Error al subir imagen: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // Método para subir el PDF al almacenamiento de Firebase
    private fun uploadPdfToStorage(uid: String, nombre: String, rut: String, correo: String, telefono: String, profesion: String, imageUrl: String?) {
        val ref = storage.reference.child("pdfs/$uid.pdf")
        val uploadTask = ref.putFile(selectedPdfUri!!)

        uploadTask.addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener { uri ->
                saveAdditionalUserData(uid, nombre, rut, correo, telefono, profesion, imageUrl, uri.toString())
            }
        }.addOnFailureListener { e ->
            Log.e("RegisterSpecialist", "Error al subir PDF", e)
            Toast.makeText(this, "Error al subir PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // Método para guardar datos adicionales del usuario
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
        // Referencia a la subcolección "especialistas" directamente dentro de la colección "especialistas"
        val specialistRef = firestore.collection("especialistas").document(uid)

        specialistRef.set(user)
            .addOnSuccessListener {
                Log.d("RegisterSpecialist", "DocumentSnapshot successfully written!")
                loader.visibility = View.INVISIBLE
                navigateToLogin()
            }
            .addOnFailureListener { e ->
                Log.w("RegisterSpecialist", "Error writing document", e)
            }
    }

    // Método para navegar a la pantalla de inicio de sesión
    private fun navigateToLogin() {
        val intent = Intent(this, Login::class.java) // Asegúrate de que esta es la clase correcta para la pantalla de inicio de sesión
        startActivity(intent)
        finish()
    }

    // Función para volver atrás
    fun backMenu(view: View) {
        super.onBackPressed()
    }
}
