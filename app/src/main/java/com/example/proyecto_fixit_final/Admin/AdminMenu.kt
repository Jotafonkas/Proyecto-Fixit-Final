package com.example.proyecto_fixit_final.Admin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto_fixit_final.Login
import com.example.proyecto_fixit_final.R
import com.google.firebase.auth.FirebaseAuth

class AdminMenu : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_menu)

        // Botones en el layout con los ids 'rectangle1', 'rectangle2', 'rectangle3', y 'rectangle4'
        findViewById<View>(R.id.rectangle1).setOnClickListener { goClients() }
        findViewById<View>(R.id.rectangle2).setOnClickListener { goSpecialist() }
        findViewById<View>(R.id.rectangle3).setOnClickListener { goSolicitudes() }
        findViewById<View>(R.id.rectangle4).setOnClickListener { mostrarDialogoConfirmacion() }
    }

    // Función para ir a la lista de clientes
    private fun goClients() {
        val intent = Intent(this, AdminListClients::class.java)
        startActivity(intent)
    }

    // Función para ir a la lista de especialistas
    private fun goSpecialist() {
        val intent = Intent(this, AdminListSpecialists::class.java)
        startActivity(intent)
    }

    // Función para ir a las solicitudes de servicios
    private fun goSolicitudes() {
        val intent = Intent(this, AdminSolicitudeServices::class.java)
        startActivity(intent)
    }

    // Función para ir a la autenticación de especialistas
    fun goAuthorizeSpecialists(view: View) {
        val intent = Intent(this, AdminAuthSpecialist::class.java)
        startActivity(intent)
    }

    // Función para mostrar el diálogo de confirmación antes de cerrar sesión
    private fun mostrarDialogoConfirmacion() {
        // Creamos un diálogo de alerta
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cerrar Sesión")
        builder.setMessage("¿Está seguro que desea cerrar sesión?")

        // Botones del diálogo
        builder.setPositiveButton("Sí") { dialog, _ ->
            cerrarSesion()
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    // Función para cerrar sesión
    private fun cerrarSesion() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        Toast.makeText(this, "Sesión cerrada exitosamente", Toast.LENGTH_SHORT).show()
        finish()
    }
}
