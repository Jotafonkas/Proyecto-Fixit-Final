package com.example.proyecto_fixit_final.Specialist

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto_fixit_final.Login
import com.example.proyecto_fixit_final.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class RegisterSpecialist : AppCompatActivity() {

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
    private lateinit var ciudadEsp: AutoCompleteTextView
    private lateinit var passEsp: EditText
    private lateinit var pass2Esp: EditText
    private lateinit var loaderDialog: Dialog
    private var selectedImageUri: Uri? = null
    private var selectedPdfUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registro_especialista)
        auth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()
        storage = Firebase.storage

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
        ciudadEsp = findViewById(R.id.autoCompleteTextView)
        passEsp = findViewById(R.id.edContraseña_especialista)
        pass2Esp = findViewById(R.id.edConfirmaContraseña_especialista)

        setupLoader()
        // Formatear el RUT al perder el foco
        rutEsp.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val rut = rutEsp.text.toString()
                if (rut.isNotEmpty()) {
                    val rutFormateado = formatRut(rut)
                    rutEsp.setText(rutFormateado)
                }
            }
        }

        // Configurar el menú desplegable para la ciudad
        val cities = resources.getStringArray(R.array.simple_items)
        val adapter = ArrayAdapter(this, R.layout.list_item, cities)
        ciudadEsp.setAdapter(adapter)

        ciudadEsp.setOnClickListener {
            ciudadEsp.showDropDown()
        }

        btnRegistro.setOnClickListener {
            // Obtener datos del registro
            val nombre: String = nombreEsp.text.toString()
            val rut: String = rutEsp.text.toString()
            val correo: String = correoEsp.text.toString()
            val telefono: String = telefonoEsp.text.toString()
            val profesion: String = profesionEsp.text.toString()
            val ciudad: String = ciudadEsp.text.toString()
            val pass: String = passEsp.text.toString()
            val pass2: String = pass2Esp.text.toString()

            // Validaciones
            if (nombre.isEmpty() || !nombre.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ ]+$"))) {
                nombreEsp.error = "Ingrese su nombre (solo letras)"
                return@setOnClickListener
            }

            if (validarRutKE(rut)) {
                rutEsp.error = "Ingrese un RUT válido"
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

            if (profesion.isEmpty() || !profesion.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ ]+$"))) {
                profesionEsp.error = "Ingrese su profesion/especialidad (solo letras)"
                return@setOnClickListener
            }

            if (ciudad.isEmpty()) {
                ciudadEsp.error = "Seleccione su ciudad"
                return@setOnClickListener
            }

            var isPasswordValid = true

            if (pass.isEmpty() || !pass.matches(Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=.,_*])(?=\\S+$).{6,}$"))) {
                passEsp.error = "Ingrese su contraseña (al menos 6 caracteres, 1 número, 1 letra minúscula, 1 letra mayúscula y 1 carácter especial)"
                isPasswordValid = false
            }

            if (pass2.isEmpty() || !pass2.matches(Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=.,_*])(?=\\S+$).{6,}$"))) {
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
                        showLoader()
                        registrarNuevoUsuario(nombre, rut, correo, telefono, profesion, ciudad, pass)
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
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png", "image/webp"))
            }
            startActivityForResult(Intent.createChooser(intent, "Seleccionar Imagen"), REQUEST_CODE_SELECT_IMAGE)
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

    private fun validarRut(rut: String, callback: (Boolean) -> Unit) {
        firestore.collection("especialistas")
            .whereEqualTo("rut", rut)
            .get()
            .addOnSuccessListener { documents ->
                callback(documents.isEmpty)
            }
            .addOnFailureListener { exception ->
                hideLoader()
                Log.e("RegisterSpecialist", "Error al validar el RUT: ${exception.message}", exception)
                callback(false)
            }
    }

    private fun registrarNuevoUsuario(nombre: String, rut: String, correo: String, telefono: String, profesion: String, ciudad: String, pass: String) {
        auth.createUserWithEmailAndPassword(correo, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.sendEmailVerification()?.addOnCompleteListener { verificationTask ->
                        if (verificationTask.isSuccessful) {
                            user.let {
                                if (selectedImageUri != null) {
                                    uploadImageToStorage(it.uid, nombre, rut, correo, telefono, profesion, ciudad)
                                }
                            }
                        } else {
                            hideLoader()
                            Toast.makeText(this, "Error al enviar el correo de verificación: ${verificationTask.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    hideLoader()
                    Toast.makeText(this, "Error al crear usuario: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun uploadImageToStorage(uid: String, nombre: String, rut: String, correo: String, telefono: String, profesion: String, ciudad: String) {
        val ref = storage.reference.child("images/$uid.jpg")
        val uploadTask = ref.putFile(selectedImageUri!!)

        uploadTask.addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener { uri ->
                if (selectedPdfUri != null) {
                    uploadPdfToStorage(uid, nombre, rut, correo, telefono, profesion, ciudad, uri.toString())
                } else {
                    saveAdditionalUserData(uid, nombre, rut, correo, telefono, profesion, ciudad, uri.toString(), null)
                }
            }
        }.addOnFailureListener { e ->
            Log.e("RegisterSpecialist", "Error al subir imagen", e)
            Toast.makeText(this, "Error al subir imagen: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun uploadPdfToStorage(uid: String, nombre: String, rut: String, correo: String, telefono: String, profesion: String, ciudad: String, imageUrl: String) {
        val ref = storage.reference.child("pdfs/$uid.pdf")
        val uploadTask = ref.putFile(selectedPdfUri!!)

        uploadTask.addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener { uri ->
                saveAdditionalUserData(uid, nombre, rut, correo, telefono, profesion, ciudad, imageUrl, uri.toString())
            }
        }.addOnFailureListener { e ->
            Log.e("RegisterSpecialist", "Error al subir PDF", e)
            Toast.makeText(this, "Error al subir PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun saveAdditionalUserData(uid: String, nombre: String, rut: String, correo: String, telefono: String, profesion: String, ciudad: String, imageUrl: String?, pdfUrl: String?) {
        val user = hashMapOf(
            "nombre" to nombre,
            "rut" to rut,
            "correo" to correo,
            "telefono" to telefono,
            "profesion" to profesion,
            "ciudad" to ciudad,
            "imageUrl" to imageUrl,
            "pdfUrl" to pdfUrl
        )

        firestore.collection("especialistas").document(uid)
            .set(user)
            .addOnSuccessListener {
                hideLoader()
                showVerificationDialog(correo)
            }
            .addOnFailureListener { e ->
                hideLoader()
                Log.e("RegisterSpecialist", "Error al guardar datos adicionales del usuario", e)
                Toast.makeText(this, "Error al guardar datos adicionales del usuario: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun showVerificationDialog(email: String?) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Correo de verificación enviado")
        builder.setMessage("Se ha enviado un correo de verificación a $email. Por favor, verifica tu correo electrónico antes de iniciar sesión.")
        builder.setPositiveButton("OK") { dialog, _ ->
            navigateToLogin()
        }
        val dialog = builder.create()
        dialog.show()
    }
    private fun validarRutKE(rut: String): Boolean {
        val rutLimpio = rut.replace(".", "").replace("-", "")
        if (rutLimpio.length < 8 || rutLimpio.length > 9) {
            return false
        }

        val cuerpo = rutLimpio.dropLast(1)
        val dv = rutLimpio.last()

        return calcularDV(cuerpo) == dv
    }

    // Método para calcular el dígito verificador (DV) de un RUT
    private fun calcularDV(rut: String): Char {
        var suma = 0
        var multiplo = 2

        rut.reversed().forEach { c ->
            suma += c.toString().toInt() * multiplo
            if (multiplo == 7) {
                multiplo = 2
            } else {
                multiplo++
            }
        }

        val dvEsperado = 11 - (suma % 11)
        return when (dvEsperado) {
            11 -> '0'
            10 -> 'K'
            else -> dvEsperado.toString().first()
        }
    }

    private fun formatRut(rut: String): String {
        val cleanedRut = rut.replace(Regex("[^0-9kK]"), "")
        val rutPart = cleanedRut.substring(0, cleanedRut.length - 1)
        val dvPart = cleanedRut.last().toUpperCase()

        val formattedRutPart = StringBuilder()
        var count = 0

        for (i in rutPart.length - 1 downTo 0) {
            formattedRutPart.append(rutPart[i])
            count++
            if (count == 3 && i != 0) {
                formattedRutPart.append(".")
                count = 0
            }
        }

        return formattedRutPart.reverse().toString() + "-" + dvPart
    }

    private fun navigateToLogin() {
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish()
    }

    fun backMenu(view: View) {
        super.onBackPressed()
    }
}
