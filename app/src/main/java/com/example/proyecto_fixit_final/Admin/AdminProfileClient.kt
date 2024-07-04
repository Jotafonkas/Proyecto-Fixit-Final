package com.example.proyecto_fixit_final.Admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto_fixit_final.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class AdminProfileClient : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var imageUrl: String // Variable para almacenar la URL de la imagen actual

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_perfil_cliente)

        // Recibir datos del Intent
        val nombre = intent.getStringExtra("nombre")
        imageUrl = intent.getStringExtra("imageUrl") ?: ""
        val rut = intent.getStringExtra("rut")
        val correo = intent.getStringExtra("correo")
        val telefono = intent.getStringExtra("telefono")
        val clientId = intent.getStringExtra("clientId") // Obtener el ID del cliente

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance()

        // Configurar vistas
        val nombreTextView: TextView = findViewById(R.id.edNombre)
        val imagenImageView: ImageView = findViewById(R.id.imgPerfilCliente)
        val rutTextView: TextView = findViewById(R.id.edRut)
        val correoTextView: TextView = findViewById(R.id.edCorreo)
        val telefonoTextView: TextView = findViewById(R.id.edTelefono)
        val btnEliminar: Button = findViewById(R.id.btnEliminar)
        val btnEliminarPerfilCliente: Button = findViewById(R.id.btnEliminarPerfilCliente)

        nombreTextView.text = nombre
        rutTextView.text = rut
        correoTextView.text = correo
        telefonoTextView.text = telefono

        if (!imageUrl.isNullOrEmpty()) {
            Picasso.get().load(imageUrl).into(imagenImageView)
        } else {
            imagenImageView.setImageResource(R.drawable.imagen_login) // Imagen predeterminada
        }

        // Configurar botones para volver al sitio indicado
        val flechaVolver: ImageView = findViewById(R.id.flechavolver_listaclientes)
        val menu: TextView = findViewById(R.id.menu)

        flechaVolver.setOnClickListener { irAListaClientes() }
        menu.setOnClickListener { irAlMenu() }

        // Configurar acción de eliminar foto con confirmación
        btnEliminar.setOnClickListener { mostrarConfirmacionEliminarFoto() }

        // Configurar acción de eliminar perfil de cliente con confirmación
        btnEliminarPerfilCliente.setOnClickListener { mostrarConfirmacionEliminarCliente(clientId) }
    }

    // Función para mostrar la confirmación de eliminación de perfil de cliente
    private fun mostrarConfirmacionEliminarCliente(clientId: String?) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmación")
        builder.setMessage("¿Está seguro de eliminar este usuario?")

        builder.setPositiveButton("Eliminar") { dialog, which ->
            eliminarPerfilCliente(clientId)
        }

        builder.setNegativeButton("Cancelar") { dialog, which ->
            dialog.dismiss()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    // Función para mostrar la confirmación de eliminación de foto
    private fun mostrarConfirmacionEliminarFoto() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmación")
        builder.setMessage("¿Está seguro de eliminar la foto del usuario?")

        builder.setPositiveButton("Eliminar") { dialog, which ->
            eliminarFotoCliente()
        }

        builder.setNegativeButton("Cancelar") { dialog, which ->
            dialog.dismiss()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    // Función para eliminar la foto del cliente
    private fun eliminarFotoCliente() {
        if (!imageUrl.isNullOrEmpty()) {
            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.getReferenceFromUrl(imageUrl)

            storageRef.delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Imagen eliminada correctamente", Toast.LENGTH_SHORT).show()
                    val imagenImageView: ImageView = findViewById(R.id.imgPerfilCliente)
                    imagenImageView.setImageResource(R.drawable.imagen_login) // Imagen predeterminada

                    // Actualizar la URL de la imagen en Firestore
                    val clientId = intent.getStringExtra("clientId")
                    if (!clientId.isNullOrEmpty()) {
                        val clienteRef = db.collection("clientes").document(clientId)
                        clienteRef.update("imageUrl", null)
                            .addOnSuccessListener {
                                Log.d("ProfileClientAdmin", "URL de imagen actualizada en Firestore")
                                imageUrl = "" // Limpiar la URL de la imagen localmente
                            }
                            .addOnFailureListener { e ->
                                Log.e("ProfileClientAdmin", "Error al actualizar URL de imagen: ${e.message}")
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("ProfileClientAdmin", "Error al eliminar imagen: ${e.message}")
                    Toast.makeText(this, "Error al eliminar imagen", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "No se puede eliminar la imagen", Toast.LENGTH_SHORT).show()
        }
    }

    // Función para eliminar el perfil del cliente
    private fun eliminarPerfilCliente(clientId: String?) {
        if (!clientId.isNullOrEmpty()) {
            db.collection("clientes").document(clientId)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Perfil de cliente eliminado correctamente", Toast.LENGTH_SHORT).show()
                    irAListaClientes()
                }
                .addOnFailureListener { e ->
                    Log.e("ProfileClientAdmin", "Error al eliminar perfil del cliente: ${e.message}")
                    Toast.makeText(this, "Error al eliminar perfil del cliente", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "No se puede eliminar el perfil del cliente", Toast.LENGTH_SHORT).show()
        }
    }

    // Función para volver a la lista de clientes
    private fun irAListaClientes() {
        val intent = Intent(this, AdminListClients::class.java)
        startActivity(intent)
        finish()
    }

    // Función para volver al menú
    private fun irAlMenu() {
        val intent = Intent(this, AdminMenu::class.java)
        startActivity(intent)
        finish()
    }
}
