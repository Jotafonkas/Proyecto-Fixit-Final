package com.example.proyecto_fixit_final.Client

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
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

@Suppress("DEPRECATION")
class ProfileClient : AppCompatActivity() {
    private lateinit var edCorreo: TextView
    private lateinit var edRut: TextView
    private lateinit var edNombre: EditText
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
    private lateinit var btnEditTelefono: ImageButton

    // Función para cargar la vista de perfil del cliente
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.perfil_cliente)
        auth = FirebaseAuth.getInstance() // Inicializar Firebase
        firestore = Firebase.firestore  // Inicializar Firestore
        edCorreo = findViewById(R.id.edCorreo) // Obtener el campo de correo
        edRut = findViewById(R.id.edRut) // Obtener el campo de RUT
        edNombre = findViewById(R.id.edNombre) // Obtener el campo de nombre
        edTelefono = findViewById(R.id.edTelefono) // Obtener el campo de teléfono
        imgperfil = findViewById(R.id.imgPerfilCliente) // Obtener la imagen de perfil
        btnUpload = findViewById(R.id.btnFoto) // Obtener el botón de subir foto
        btnDelete = findViewById(R.id.btnEliminar) // Obtener el botón de eliminar foto
        btnSave = findViewById(R.id.btnGuardarPerfilCliente) // Obtener el botón de guardar perfil
        storage = FirebaseStorage.getInstance() // Inicializar Firebase Storage
        storageReference = storage.reference // Inicializar la referencia de Storage
        btnEditNombre = findViewById(R.id.btnEditNombre) // Obtener el botón de editar nombre
        btnEditTelefono = findViewById(R.id.btnEditTelefono) // Obtener el botón de editar teléfono

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

        btnEditTelefono.setOnClickListener {
            edTelefono.isFocusableInTouchMode = true
            edTelefono.isFocusable = true
            edTelefono.requestFocus()
        }

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
            "telefono" to telefono
        )

        firestore.collection("clientes").document(uid!!)
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
        super.onBackPressed()
    }

    // Función para obtener y mostrar los datos del usuario
    private fun fetchAndDisplayData(uid: String) {
        firestore.collection("clientes").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val nombre = document.getString("nombre")
                    val correo = document.getString("correo")
                    val rut = document.getString("rut")
                    val telefono = document.getString("telefono")
                    val imageUrl = document.getString("imageUrl")
                    edNombre.setText(nombre)
                    edCorreo.text = correo
                    edRut.text = rut
                    edTelefono.setText(telefono)
                    if (!imageUrl.isNullOrEmpty()) {
                        Picasso.get().load(imageUrl).into(imgperfil)
                    }
                    Log.d("ProfileClient", "Datos del usuario: $document")
                } else {
                    Toast.makeText(this, "No se encontró el usuario.", Toast.LENGTH_LONG).show()
                    Log.d("ProfileClient", "No se encontró el usuario con el ID: $uid")
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al obtener el usuario: ${e.message}", Toast.LENGTH_LONG).show()
                Log.d("ProfileClient", "Error al obtener el usuario: ${e.message}")
            }
    }
}