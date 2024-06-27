package com.example.proyecto_fixit_final

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import com.example.proyecto_fixit_final.Admin.MenuAdmin
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.portada)

        // Inicializar Firebase Auth y Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Verificar el estado de inicio de sesión
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        val currentUser = auth.currentUser
        val sharedPref = getSharedPreferences("userPrefs", MODE_PRIVATE)
        val userType = sharedPref.getString("userType", null)

        if (currentUser != null && currentUser.isEmailVerified && userType != null) {
            when (userType) {
                "admin" -> {
                    startActivity(Intent(this, MenuAdmin::class.java))
                }
                "specialist" -> {
                    startActivity(Intent(this, NavBar::class.java))
                }
                "client" -> {
                    startActivity(Intent(this, NavBarClient::class.java))
                }
                else -> {
                    showErrorDialog("Tipo de usuario no reconocido")
                }
            }
            finish()
        }
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    fun openLogin(view: View) {
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
    }

    fun openSelectUser(view: View) {
        val intent = Intent(this, SelectUser::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setMessage("¿Estás seguro de que quieres salir?")
            .setCancelable(false)
            .setPositiveButton("Sí") { dialog, id ->
                super.onBackPressed()
                finishAffinity()
            }
            .setNegativeButton("No", null)
            .show()
    }
}
