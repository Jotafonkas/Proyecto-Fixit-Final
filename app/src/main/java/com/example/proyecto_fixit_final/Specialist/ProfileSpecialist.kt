package com.example.proyecto_fixit_final.Specialist

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
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

class ProfileSpecialist : AppCompatActivity() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.perfil_especialista)

        setContentView(R.layout.perfil_especialista)
        auth = FirebaseAuth.getInstance()
        firestore = Firebase.firestore
        edCorreo = findViewById(R.id.edCorreo)
        edRut = findViewById(R.id.edRut)
        edNombre = findViewById(R.id.edNombre)
        edProfesion = findViewById(R.id.edEspecialidad)
        edTelefono = findViewById(R.id.edTelefono)
        edCorreo = findViewById(R.id.edCorreo)
        imgperfil = findViewById(R.id.imgPerfilEspecialista)
        btnUpload = findViewById(R.id.btnFoto)
        btnDelete = findViewById(R.id.btnEliminar)
        btnSave = findViewById(R.id.btnGuardarPerfilEspecialista)
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

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

        // Recibir el correo pasado desde HomeActivity
        val correo = intent.getStringExtra("correo")

        // Poner el correo en el campo de EditText
        val user = auth.currentUser
        if (user != null) {
            // Recuperar y mostrar el nombre del usuario desde Firestore
            fetchAndDisplayData(user.uid)
        }
    }

    private fun loadProfileImage(uid: String?) {
        storageReference.child("images/$uid.jpg").downloadUrl.addOnSuccessListener {
            Picasso.get().load(it).into(imgperfil)
        }.addOnFailureListener {
            Toast.makeText(this, "Error al cargar la imagen.", Toast.LENGTH_LONG).show()
        }
    }

    private fun deleteProfileImage(uid: String?) {
        storageReference.child("images/$uid.jpg").delete().addOnSuccessListener {
            imgperfil.setImageDrawable(null)
            Toast.makeText(this, "Imagen eliminada con éxito.", Toast.LENGTH_LONG).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Error al eliminar la imagen.", Toast.LENGTH_LONG).show()
        }
    }

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
        if (rut.isEmpty()) {
            edRut.error = "Ingrese su rut"
            return
        }
        if (profesion.isEmpty()) {
            edProfesion.error = "Ingrese su profesion/especialidad"
            return
        }
        if (telefono.isEmpty()) {
            edTelefono.error = "Ingrese su telefono"
            return
        }

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

    fun backMenu(view: View) {
        val intent = Intent(this, MenuFragment::class.java)
        startActivity(intent)
    }

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