package com.example.proyecto_fixit_final

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto_fixit_final.Admin.AdminMenu
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Login : AppCompatActivity() {

    private lateinit var Email_Login_Specialist: TextInputEditText
    private lateinit var password_login_specialist: TextInputEditText
    private lateinit var btn_login_specialist: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var forgot_password: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_login)

        // Inicializar Firebase Auth y Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        Email_Login_Specialist = findViewById(R.id.Email_Login_Specialist) // Obtener el correo
        password_login_specialist = findViewById(R.id.password_login_specialist) // Obtener la contraseña
        btn_login_specialist = findViewById(R.id.btn_login_specialist) // Obtener el botón
        forgot_password = findViewById(R.id.forgot_password) // Obtener el TextView

        // login en click al botón
        btn_login_specialist.setOnClickListener {
            // recuperar datos
            val correo = Email_Login_Specialist.text.toString()
            val pass = password_login_specialist.text.toString()

            if (correo.isEmpty()) {
                Email_Login_Specialist.error = "Ingrese un correo"
                return@setOnClickListener
            }
            if (pass.isEmpty()) {
                password_login_specialist.error = "Ingrese su contraseña"
                return@setOnClickListener
            }
            iniciarSesion(correo, pass)
        }

        // Recuperar contraseña en click al TextView
        forgot_password.setOnClickListener {
            val correo = Email_Login_Specialist.text.toString()
            if (correo.isEmpty()) {
                Email_Login_Specialist.error = "Ingrese un correo"
                return@setOnClickListener
            }
            recuperarContrasena(correo)
        }
    }

    // Iniciar sesión
    private fun iniciarSesion(correo: String, pass: String) {
        auth.signInWithEmailAndPassword(correo, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user?.isEmailVerified == true) {
                        user?.let {
                            val uid = it.uid
                            verificarRolUsuario(uid, correo, pass)
                        }
                    } else {
                        // El correo no está verificado, mostrar mensaje de error en ventana emergente
                        showVerificationRequiredDialog()
                        auth.signOut()
                    }
                } else {
                    Toast.makeText(this, "Error al iniciar sesión: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun showVerificationRequiredDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Verificación de correo requerida")
        builder.setMessage("Por favor, verifique su correo electrónico antes de iniciar sesión.")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    // Verificar rol de usuario en Firestore
    private fun verificarRolUsuario(uid: String, correo: String, pass: String) {
        // Verificar si el usuario es un admin
        val adminRef = firestore.collection("admin").document("admin_credentials")
        adminRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val adminEmail = document.getString("email")
                val adminPassword = document.getString("password")
                if (correo == adminEmail && pass == adminPassword) {
                    // Usuario es un admin
                    guardarTipoUsuario("admin")
                    val intent = Intent(this, AdminMenu::class.java)
                    intent.putExtra("uid", uid)
                    startActivity(intent)
                    finish()
                    return@addOnSuccessListener
                }
            }
            // Si no es admin, verificar si es especialista o cliente
            verificarEspecialistaOCliente(uid)
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Error al obtener los datos del usuario: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // Verificar si el usuario es un especialista o un cliente
    private fun verificarEspecialistaOCliente(uid: String) {
        val especialistasRef = firestore.collection("especialistas").document(uid)
        especialistasRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                // Usuario es un especialista
                guardarTipoUsuario("specialist")
                val intent = Intent(this, NavBarSpecialist::class.java)
                intent.putExtra("uid", uid)
                startActivity(intent)
                finish()
            } else {
                // Usuario no es un especialista, verificar en clientes
                val clientesRef = firestore.collection("clientes").document(uid)
                clientesRef.get().addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // Usuario es un cliente
                        guardarTipoUsuario("client")
                        val intent = Intent(this, NavBarClient::class.java)
                        intent.putExtra("uid", uid)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "No se encontró el usuario en Firestore", Toast.LENGTH_LONG).show()
                    }
                }.addOnFailureListener { e ->
                    Toast.makeText(this, "Error al obtener los datos del usuario: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Error al obtener los datos del usuario: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // Guardar tipo de usuario en SharedPreferences
    private fun guardarTipoUsuario(tipo: String) {
        val sharedPref = getSharedPreferences("userPrefs", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("userType", tipo)
            apply()
        }
    }

    // Recuperar contraseña
    private fun recuperarContrasena(correo: String) {
        auth.sendPasswordResetEmail(correo)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Mostrar alerta de éxito
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Correo enviado")
                    builder.setMessage("Correo para restablecer contraseña enviado a $correo")
                    builder.setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    val dialog = builder.create()
                    dialog.show()
                } else {
                    // Mostrar alerta de error
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Error")
                    builder.setMessage("Error al enviar correo: ${task.exception?.message}")
                    builder.setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    val dialog = builder.create()
                    dialog.show()
                }
            }
    }

    fun backMenu(view: View) {
        super.onBackPressed()
    }
}
