package com.example.proyecto_fixit_final.Specialist

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto_fixit_final.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

@Suppress("DEPRECATION")
class SpecialistProfile : AppCompatActivity() {
    // Declaración de variables
    private lateinit var edCorreo: TextView
    private lateinit var edRut: TextView
    private lateinit var edNombre: EditText
    private lateinit var edProfesion: EditText
    private lateinit var edTelefono: EditText
    private lateinit var spinnerCiudad: Spinner
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
    private lateinit var btnEditEspecialidad: ImageButton
    private lateinit var btnEditCiudad: ImageButton // Nuevo botón para editar ciudad

    private var ciudadSeleccionada: String? = null // Variable para almacenar la ciudad seleccionada

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.especialista_perfil)
        auth = FirebaseAuth.getInstance() // Inicializar Firebase Auth
        firestore = Firebase.firestore  // Inicializar Firestore
        storage = FirebaseStorage.getInstance() // Inicializar Firebase Storage
        storageReference = storage.reference // Inicializar la referencia de Storage

        edCorreo = findViewById(R.id.edCorreo)
        edRut = findViewById(R.id.edRut)
        edNombre = findViewById(R.id.edNombre)
        edProfesion = findViewById(R.id.edEspecialidad)
        edTelefono = findViewById(R.id.edTelefono)
        spinnerCiudad = findViewById(R.id.spinnerCiudad)
        imgperfil = findViewById(R.id.imgPerfilEspecialista)
        btnUpload = findViewById(R.id.btnFoto)
        btnDelete = findViewById(R.id.btnEliminar)
        btnSave = findViewById(R.id.btnGuardarPerfilEspecialista)
        btnEditNombre = findViewById(R.id.btnEditNombre)
        btnEditTelefono = findViewById(R.id.btnEditTelefono)
        btnEditEspecialidad = findViewById(R.id.btnEditEspecialidad)
        btnEditCiudad = findViewById(R.id.btnEditCiudad) // Inicializar el botón de editar ciudad

        loadProfileImage(auth.currentUser?.uid)

        btnUpload.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png", "image/webp"))
            }
            startActivityForResult(Intent.createChooser(intent, "Seleccionar Imagen"), REQUEST_CODE_SELECT_IMAGE)
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

        btnEditEspecialidad.setOnClickListener {
            edProfesion.isFocusableInTouchMode = true
            edProfesion.isFocusable = true
            edProfesion.requestFocus()
        }

        btnEditCiudad.setOnClickListener {
            spinnerCiudad.isEnabled = true
            spinnerCiudad.performClick()
        }

        val ciudadArray = resources.getStringArray(R.array.simple_items)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ciudadArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCiudad.adapter = adapter
        spinnerCiudad.isEnabled = false  // Disable spinner initially

        spinnerCiudad.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                ciudadSeleccionada = parent.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                ciudadSeleccionada = null
            }
        }

        // Poner el correo en el campo de EditText
        val user = auth.currentUser
        if (user != null) {
            // Recuperar y mostrar el nombre del usuario desde Firestore
            fetchAndDisplayData(user.uid)
        }
    }

    private fun loadProfileImage(uid: String?) {
        val extensions = listOf("jpg", "jpeg", "png", "webp")
        extensions.forEach { ext ->
            storageReference.child("images/$uid.$ext").downloadUrl.addOnSuccessListener {
                Picasso.get().load(it).into(imgperfil)
                return@addOnSuccessListener
            }.addOnFailureListener {
                // Continue checking other extensions
            }
        }
    }

    private fun deleteProfileImage(uid: String?) {
        val extensions = listOf("jpg", "jpeg", "png", "webp")
        extensions.forEach { ext ->
            storageReference.child("images/$uid.$ext").delete().addOnSuccessListener {
                imgperfil.setImageDrawable(null)
                Toast.makeText(this, "Imagen eliminada con éxito.", Toast.LENGTH_LONG).show()
                return@addOnSuccessListener
            }.addOnFailureListener {
                // Continue checking other extensions
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK && data != null && data.data != null) {
            val filePath = data.data
            val ref = storage.reference.child("images/${auth.currentUser?.uid}.${getFileExtension(filePath!!)}")
            ref.putFile(filePath).addOnSuccessListener {
                Toast.makeText(this, "Imagen cargada con éxito.", Toast.LENGTH_LONG).show()
                loadProfileImage(auth.currentUser?.uid)
            }.addOnFailureListener {
                Toast.makeText(this, "Error al cargar la imagen.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getFileExtension(uri: Uri): String? {
        return contentResolver.getType(uri)?.substringAfterLast('/')
    }

    fun formatRUT(rut: String): String {
        var rutFormatted = rut
        // Remove any non-digit characters except for 'K' or 'k'
        rutFormatted = rutFormatted.replace(Regex("[^\\dKk]"), "")

        // Split the RUT into the body and the check digit
        val body = rutFormatted.dropLast(1)
        val checkDigit = rutFormatted.takeLast(1)

        // Format the body with dots
        val formattedBody = body.reversed().chunked(3).joinToString(".").reversed()

        // Combine the formatted body with the check digit
        return "$formattedBody-$checkDigit"
    }

    private fun updateUserData(uid: String?) {
        val nombre = edNombre.text.toString()
        val rut = edRut.text.toString()
        val profesion = edProfesion.text.toString()
        val telefono = edTelefono.text.toString()
        val ciudad = ciudadSeleccionada // Obtener la ciudad seleccionada

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

        if (ciudad.isNullOrEmpty()) {
            Toast.makeText(this, "Seleccione una ciudad", Toast.LENGTH_SHORT).show()
            return
        }

        // Actualizar los datos del usuario en Firestore
        val userUpdates: MutableMap<String, Any> = hashMapOf(
            "nombre" to nombre,
            "rut" to rut,
            "profesion" to profesion,
            "telefono" to telefono,
            "ciudad" to ciudad // Actualizar la  ciudad en Firestore
        )

        firestore.collection("especialistas").document(uid!!)
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

    private fun fetchAndDisplayData(uid: String) {
        firestore.collection("especialistas").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val nombre = document.getString("nombre")
                    val correo = document.getString("correo")
                    val rut = document.getString("rut")
                    val profesion = document.getString("profesion")
                    val telefono = document.getString("telefono")
                    val ciudad = document.getString("ciudad") // Obtener la ciudad
                    val imageUrl = document.getString("imageUrl")
                    edNombre.setText(nombre)
                    edCorreo.setText(correo)
                    edRut.setText(rut?.let { formatRUT(it) })
                    edProfesion.setText(profesion)
                    edTelefono.setText(telefono)

                    val position = (spinnerCiudad.adapter as ArrayAdapter<String>).getPosition(ciudad)
                    spinnerCiudad.setSelection(position) // Seleccionar la ciudad en el spinner

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

    companion object {
        private const val REQUEST_CODE_SELECT_IMAGE = 123
    }
}
