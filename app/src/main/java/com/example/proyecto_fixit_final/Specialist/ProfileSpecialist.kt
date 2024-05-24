package com.example.proyecto_fixit_final.Specialist

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto_fixit_final.R
import com.example.proyecto_fixit_final.fragments.MenuFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

@Suppress("DEPRECATION")
class ProfileSpecialist : AppCompatActivity() {
    // Declaración de variables
    private lateinit var edCorreo: TextView
    private lateinit var edRut: EditText
    private lateinit var edNombre: EditText
    private lateinit var edProfesion: EditText
    private lateinit var edTelefono: EditText
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var imgperfil: ImageView
    private lateinit var btnUpload: Button
    private lateinit var btnDelete: Button
    private lateinit var btnSave: Button
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private lateinit var btnEditNombre: ImageButton
    private lateinit var btnEditRut: ImageButton
    private lateinit var btnEditTelefono: ImageButton
    private lateinit var btnEditEspecialidad: ImageButton

    // Función para cargar la vista de perfil del especialista
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.perfil_especialista)
        auth = FirebaseAuth.getInstance() // Inicializar Firebase
        firestore = Firebase.firestore  // Inicializar Firestore
        edCorreo = findViewById(R.id.edCorreo) // Obtener el campo de correo
        edRut = findViewById(R.id.edRut) // Obtener el campo de RUT
        edNombre = findViewById(R.id.edNombre) // Obtener el campo de nombre
        edProfesion = findViewById(R.id.edEspecialidad) // Obtener el campo de especialidad
        edTelefono = findViewById(R.id.edTelefono) // Obtener el campo de teléfono
        edCorreo = findViewById(R.id.edCorreo) // Obtener el campo de correo
        imgperfil = findViewById(R.id.imgPerfilEspecialista) // Obtener la imagen de perfil
        btnUpload = findViewById(R.id.btnFoto) // Obtener el botón de subir foto
        btnDelete = findViewById(R.id.btnEliminar) // Obtener el botón de eliminar foto
        btnSave = findViewById(R.id.btnGuardarPerfilEspecialista) // Obtener el botón de guardar perfil
        storage = FirebaseStorage.getInstance() // Inicializar Firebase Storage
        storageReference = storage.reference // Inicializar la referencia de Storage
        btnEditNombre = findViewById(R.id.btnEditNombre) // Obtener el botón de editar nombre
        btnEditRut = findViewById(R.id.btnEditRut) // Obtener el botón de editar RUT
        btnEditTelefono = findViewById(R.id.btnEditTelefono) // Obtener el botón de editar teléfono
        btnEditEspecialidad = findViewById(R.id.btnEditEspecialidad) // Obtener el botón de editar especialidad

        loadProfileImage(auth.currentUser?.uid)

        btnUpload.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 123)
        }

        btnDelete.setOnClickListener {
            deleteProfileImage(auth.currentUser?.uid)
        }

        btnSave.setOnClickListener {
            updateUserData(auth.currentUser?.uid)
        }

        btnEditNombre.setOnClickListener {
            edNombre.isFocusableInTouchMode = true
            edNombre.isFocusable = true
            edNombre.requestFocus()
        }

        btnEditRut.setOnClickListener {
            edRut.isFocusableInTouchMode = true
            edRut.isFocusable = true
            edRut.requestFocus()
        }

        btnEditTelefono.setOnClickListener {
            edTelefono.isFocusableInTouchMode = true
            edTelefono.isFocusable = true
            edTelefono.requestFocus()
        }

        btnEditEspecialidad.setOnClickListener {
            edProfesion.isFocusableInTouchMode = true
            edProfesion.isFocusable = true
            edProfesion.requestFocus()
        }

        // Recibir el correo pasado desde HomeActivity
        val correo = intent.getStringExtra("correo")

        // Poner el correo en el campo de EditText
        val user = auth.currentUser
        if (user != null) {
            // Recuperar y mostrar el nombre del usuario desde Firestore
            fetchAndDisplayData(user.uid)
        }
    }

    // Función para cargar la imagen de perfil
    private fun loadProfileImage(uid: String?) {
        storageReference.child("images/$uid.jpg").downloadUrl.addOnSuccessListener {
            Picasso.get().load(it).into(imgperfil)
        }.addOnFailureListener {
            Toast.makeText(this, "Error al cargar la imagen.", Toast.LENGTH_LONG).show()
        }
    }

    // Función para eliminar la imagen de perfil
    private fun deleteProfileImage(uid: String?) {
        storageReference.child("images/$uid.jpg").delete().addOnSuccessListener {
            imgperfil.setImageDrawable(null)
            Toast.makeText(this, "Imagen eliminada con éxito.", Toast.LENGTH_LONG).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Error al eliminar la imagen.", Toast.LENGTH_LONG).show()
        }
    }

    // Función para cargar la imagen de perfil
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 123 && resultCode == RESULT_OK && data != null && data.data != null) {
            val filePath = data.data
            val ref = storage.reference.child("images/${auth.currentUser?.uid}.jpg")
            ref.putFile(filePath!!).addOnSuccessListener {
                Toast.makeText(this, "Imagen cargada con éxito.", Toast.LENGTH_LONG).show()
                loadProfileImage(auth.currentUser?.uid)
            }.addOnFailureListener {
                Toast.makeText(this, "Error al cargar la imagen.", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Función para actualizar los datos del usuario
    private fun updateUserData(uid: String?) {
        val nombre = edNombre.text.toString()
        val rut = edRut.text.toString()
        val profesion = edProfesion.text.toString()
        val telefono = edTelefono.text.toString()

        // Validaciones
        if (nombre.isEmpty()) {
            edNombre.error = "Ingrese su nombre"
            return
        }

        if (!nombre.matches(Regex("^[a-zA-Z ]+$"))) {
            edNombre.error = "El nombre solo puede contener letras"
            return
        }

        if (rut.isEmpty()) {
            edRut.error = "Ingrese su rut"
            return
        }

        if (!rut.matches(Regex("^\\d+$"))) {
            edRut.error = "El RUT solo puede contener números"
            return
        }

        if (profesion.isEmpty()) {
            edProfesion.error = "Ingrese su profesion/especialidad"
            return
        }

        if (!profesion.matches(Regex("^[a-zA-Z ]+$"))) {
            edProfesion.error = "La profesión solo puede contener letras"
            return
        }

        if (telefono.isEmpty()) {
            edTelefono.error = "Ingrese su telefono"
            return
        }

        if (!telefono.matches(Regex("^\\d{9}$"))) {
            edTelefono.error = "El teléfono debe ser un número de 9 dígitos"
            return
        }
        // Actualizar los datos del usuario en Firestore
        val userUpdates: MutableMap<String, Any> = hashMapOf(
            "nombre" to nombre,
            "rut" to rut,
            "profesion" to profesion,
            "telefono" to telefono
        )

        firestore.collection("users").document(uid!!)
            .update(userUpdates)
            .addOnSuccessListener {
                Toast.makeText(this, "Perfil actualizado con éxito.", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al actualizar el perfil: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    // Función para volver al menú
    fun backMenu(view: View) {
        val intent = Intent(this, MenuFragment::class.java)
        startActivity(intent)
    }

    // Función para obtener y mostrar los datos del usuario
    private fun fetchAndDisplayData(uid: String) {
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val nombre = document.getString("nombre")
                    val correo = document.getString("correo")
                    val rut = document.getString("rut")
                    val profesion = document.getString("profesion")
                    val telefono = document.getString("telefono")
                    val imageUrl = document.getString("imageUrl")
                    edNombre.setText(nombre)
                    edCorreo.setText(correo)
                    edRut.setText(rut)
                    edProfesion.setText(profesion)
                    edTelefono.setText(telefono)
                    if (!imageUrl.isNullOrEmpty()) {
                        Picasso.get().load(imageUrl).into(imgperfil)
                    }
                    Log.d("ProfileSpecialist", "Datos del usuario: $document")
                } else {
                    Toast.makeText(this, "No se encontró el usuario.", Toast.LENGTH_LONG).show()
                    Log.d("ProfileSpecialist", "No se encontró el usuario con el ID: $uid")
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al obtener el usuario: ${e.message}", Toast.LENGTH_LONG).show()
                Log.d("ProfileSpecialist", "Error al obtener el usuario: ${e.message}")
            }
    }
}