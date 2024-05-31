package com.example.proyecto_fixit_final.Client

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
import com.example.proyecto_fixit_final.NavBar
import com.example.proyecto_fixit_final.NavBarClient
import com.example.proyecto_fixit_final.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage


@Suppress("DEPRECATION")
class RegisterClient: AppCompatActivity() {

    // Declarar las variables del layout
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var btnRegistro: Button
    private lateinit var btnFoto: Button
    private lateinit var btnEliminar: Button
    private lateinit var imagen: ImageView
    private lateinit var nombreCliente: EditText
    private lateinit var rutCliente: EditText
    private lateinit var correoCliente: EditText
    private lateinit var telefonoCliente: EditText
    private lateinit var passCliente: EditText
    private lateinit var pass2Cliente: EditText
    private lateinit var loader: ProgressBar
    private var selectedImageUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registro_cliente)
        auth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()
        storage = Firebase.storage

        // Declarar las variables del layout
        btnRegistro = findViewById(R.id.btnRegistrarse_cliente)
        btnFoto = findViewById(R.id.btnFoto)
        btnEliminar = findViewById(R.id.btnEliminar)
        imagen = findViewById(R.id.imagen)
        nombreCliente = findViewById(R.id.edNombre_cliente)
        rutCliente = findViewById(R.id.edRut_cliente)
        correoCliente = findViewById(R.id.edCorreo_cliente)
        telefonoCliente = findViewById(R.id.edTelefono_cliente)
        passCliente = findViewById(R.id.edContraseña_cliente)
        pass2Cliente = findViewById(R.id.edConfirmaContraseña_cliente)
        loader = findViewById(R.id.loader)

        btnRegistro.setOnClickListener {
            // Obtener datos del registro
            val nombre: String = nombreCliente.text.toString()
            val rut: String = rutCliente.text.toString()
            val correo: String = correoCliente.text.toString()
            val telefono: String = telefonoCliente.text.toString()
            val pass: String = passCliente.text.toString()
            val pass2: String = pass2Cliente.text.toString()

            // Validaciones
            if (nombre.isEmpty() || !nombre.matches(Regex("^[a-zA-Z ]+$"))) {
                nombreCliente.error = "Ingrese su nombre (solo letras)"
                return@setOnClickListener
            }

            if (rut.isEmpty() || rut.length > 9 || rut.toDoubleOrNull() == null) {
                rutCliente.error = "Ingrese su rut ( maximo 9 digitos y solo números)"
                return@setOnClickListener
            }

            if (correo.isEmpty()) {
                correoCliente.error = "Ingrese su correo"
                return@setOnClickListener
            }

            if (telefono.isEmpty() || telefono.toDoubleOrNull() == null || telefono.length != 9) {
                telefonoCliente.error = "Ingrese su telefono (9 números)"
                return@setOnClickListener
            }

            if (pass.isEmpty()) {
                passCliente.error = "Ingrese su contraseña"
                return@setOnClickListener
            }

            if (pass2.isEmpty()) {
                pass2Cliente.error = "Repita su contraseña"
                return@setOnClickListener
            }

            if (selectedImageUri == null) {
                Toast.makeText(this, "Por favor, suba su imagen de perfil.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            validarRut(rut) { isRutValido ->
                if (isRutValido) {
                    // El RUT es válido, proceder con el registro
                    if (pass == pass2) {
                        loader.visibility = View.VISIBLE
                        registrarNuevoUsuario(nombre, rut, correo, telefono, pass)
                    } else {
                        pass2Cliente.error = "Las contraseñas no coinciden"
                        Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show()
                    }
                } else {
                    rutCliente.error = "Este RUT ya está registrado"
                    Toast.makeText(this, "Este RUT ya está registrado", Toast.LENGTH_LONG).show()
                }
            }
        }

        btnFoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, RegisterClient.REQUEST_CODE_SELECT_IMAGE)
        }

        btnEliminar.setOnClickListener {
            imagen.setImageResource(R.drawable.image_perfil) // Cambia esto por el recurso predeterminado que deseas
            selectedImageUri = null
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
            }
        }
    }
    companion object {
        private const val REQUEST_CODE_SELECT_IMAGE = 1000
    }

    //metodo para validar si el rut esta repetido
    private fun validarRut(rut: String, callback: (Boolean) -> Unit) {
        firestore.collection("clientes")
            .whereEqualTo("rut", rut)
            .get()
            .addOnSuccessListener { documents ->
                callback(documents.isEmpty)
            }
            .addOnFailureListener { exception ->
                Log.e("RegisterClient", "Error al validar el RUT: ${exception.message}", exception)
                // Manejar el error apropiadamente
                callback(false)
            }
    }

    // Método para registrar un nuevo usuario en Firebase Auth
    private fun registrarNuevoUsuario(nombre: String, rut: String, correo: String, telefono: String, pass: String) {
        auth.createUserWithEmailAndPassword(correo, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        if (selectedImageUri != null) {
                            uploadImageToStorage(it.uid, nombre, rut, correo, telefono)
                        }
                    }
                } else {
                    Toast.makeText(this, "Error al crear usuario: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun uploadImageToStorage(uid: String, nombre: String, rut: String, correo: String, telefono: String) {
        val ref = storage.reference.child("images/$uid.jpg")
        val uploadTask = ref.putFile(selectedImageUri!!)

        uploadTask.addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener { uri ->

                saveAdditionalUserData(uid, nombre, rut, correo, telefono, uri.toString())
            }
        }.addOnFailureListener { e ->
            Log.e("RegisterClient", "Error al subir imagen", e)
            Toast.makeText(this, "Error al subir imagen: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // Método para guardar datos adicionales del usuario
    private fun saveAdditionalUserData(uid: String, nombre: String, rut: String, correo: String, telefono: String, imageUrl: String?) {
        val user = hashMapOf(
            "nombre" to nombre,
            "rut" to rut,
            "correo" to correo,
            "telefono" to telefono,
            "imageUrl" to imageUrl
        )
        // Referencia a la subcolección "Cliente" directamente dentro de la colección "users"
        val ClientRef = firestore.collection("clientes").document(uid)

        ClientRef.set(user)
            .addOnSuccessListener {
                Log.d("RegisterClient", "DocumentSnapshot successfully written!")
                loader.visibility = View.INVISIBLE
                navigateToHome()
            }
            .addOnFailureListener { e ->
                Log.w("RegisterClient", "Error writing document", e)
            }
    }

    // Método para navegar a la pantalla principal
    private fun navigateToHome() {
        val intent = Intent(this, NavBarClient::class.java)
        startActivity(intent)
        finish()
    }

    //funcion para volver atras
    fun backMenu(view: View) {
        super.onBackPressed()
    }
}