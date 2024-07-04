package com.example.proyecto_fixit_final.Client

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto_fixit_final.R
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class ClientChangePassword : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var edCurrentPassword: EditText
    private lateinit var edNewPassword: EditText
    private lateinit var edConfirmPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.cliente_cambiar_contra)
        auth = FirebaseAuth.getInstance()
        edCurrentPassword = findViewById(R.id.edpassactual)
        edNewPassword = findViewById(R.id.edNuevaContraseña)
        edConfirmPassword = findViewById(R.id.edConfirmarContraseña)

        val btnChangePassword = findViewById<Button>(R.id.btncambiarpassCliente)
        btnChangePassword.setOnClickListener {
            changePassword()
        }
    }

    // Funcion para cambiar la contraseña
    private fun changePassword() {
        val currentPassword = edCurrentPassword.text.toString()
        val newPassword = edNewPassword.text.toString()
        val confirmPassword = edConfirmPassword.text.toString()

        //Validaciones
        if (currentPassword.isEmpty()) {
            edCurrentPassword.error = "Ingrese su contraseña actual"
            return
        }
        if (newPassword.isEmpty()) {
            edNewPassword.error = "Ingrese su nueva contraseña"
            return
        }
        if (confirmPassword.isEmpty()) {
            edConfirmPassword.error = "Confirme su nueva contraseña"
            return
        }

        // Comparacion de contraseñas ingresadas
        if (newPassword == confirmPassword) {
            val user = auth.currentUser
            if (user != null && user.email != null) {
                val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
                user.reauthenticate(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        user.updatePassword(newPassword).addOnCompleteListener { task2 ->
                            if (task2.isSuccessful) {
                                Toast.makeText(this, "Contraseña actualizada con éxito.", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(this, "Error al actualizar la contraseña.", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "La contraseña actual es incorrecta.", Toast.LENGTH_LONG).show()
                    }
                }
            }
        } else {
            Toast.makeText(this, "La nueva contraseña y la confirmación de la contraseña no coinciden.", Toast.LENGTH_LONG).show()
        }
    }

    // Funcion para volver al menu principal
    fun backMenu(view: View) {
        super.onBackPressed()
    }
}