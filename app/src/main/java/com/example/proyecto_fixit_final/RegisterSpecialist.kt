package com.example.proyecto_fixit_final

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterSpecialist : AppCompatActivity() {

    // Declarar las variables del layout
    private lateinit var auth: FirebaseAuth
    private lateinit var btnRegistro: Button
    private lateinit var btnFoto: Button
    private lateinit var btnEliminar: Button
    private lateinit var imagen: ImageView
    private lateinit var nombreEsp: EditText
    private lateinit var rutEsp: EditText
    private lateinit var correoEsp: EditText
    private lateinit var telefonoEsp: EditText
    private lateinit var profesionEsp: EditText
    private lateinit var passEsp: EditText
    private lateinit var pass2Esp: EditText
    private var selectedImageUri: Uri? = null

    private val getImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri
                imagen.setImageURI(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registro_especialista)
        auth = Firebase.auth

        // Set variables por id
        btnRegistro = findViewById(R.id.btnRegistrarse_especialista)
        btnFoto = findViewById(R.id.btnFoto)
        btnEliminar = findViewById(R.id.btnEliminar)
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
                registrarNuevoUsuario(correo, pass)
            } else {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show()
            }
        }

        btnFoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            getImage.launch(intent)
        }

        btnEliminar.setOnClickListener {
            imagen.setImageResource(R.drawable.image_perfil) // Cambia esto por el recurso predeterminado que deseas
            selectedImageUri = null
        }
    }

    private fun registrarNuevoUsuario(correo: String, pass: String) {
        auth.createUserWithEmailAndPassword(correo, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Usuario creado correctamente, redirigir a Home
                    val intent = Intent(this, Home::class.java)
                    intent.putExtra("correo", correo)
                    startActivity(intent)
                    finish()
                } else {
                    // Error al crear usuario
                    Toast.makeText(this, "Error al crear usuario: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}

