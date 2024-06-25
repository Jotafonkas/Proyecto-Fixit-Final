package com.example.proyecto_fixit_final.Admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto_fixit_final.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.squareup.picasso.Picasso

class ViewClientsAdmin : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var clientesContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ver_clientes_admin)

        clientesContainer = findViewById(R.id.clientes_container)

        // Buscar clientes desde Firestore
        buscarClientes()

        // Configura los botones para volver al menú
        val flechaVolverMenu: ImageView = findViewById(R.id.flechavolver_menu)
        val menu: TextView = findViewById(R.id.menu)

        flechaVolverMenu.setOnClickListener { irAlMenu() }
        menu.setOnClickListener { irAlMenu() }
    }

    private fun buscarClientes() {
        db.collection("clientes")
            .get()
            .addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        val nombre = document.getString("nombre")
                        val imageUrl = document.getString("imageUrl")
                        val clientId = document.id
                        val rut = document.getString("rut")
                        val correo = document.getString("correo")
                        val telefono = document.getString("telefono")
                        addClientCard(nombre, imageUrl, clientId, rut, correo, telefono)
                    }
                } else {
                    Log.w("ViewClientsAdmin", "Error al obtener datos del cliente.", task.exception)
                }
            })
    }

    private fun addClientCard(nombre: String?, imageUrl: String?, clientId: String, rut: String?, correo: String?, telefono: String?) {
        val inflater = LayoutInflater.from(this)
        val card = inflater.inflate(R.layout.card_lista_usuarios_admin, clientesContainer, false)

        val nombreCliente: TextView = card.findViewById(R.id.nombreUsuario)
        val imagenCliente: ImageView = card.findViewById(R.id.imagen_usuario)

        nombreCliente.text = nombre

        // Verificar si la URL de la imagen ha cambiado
        if (imageUrl != null && imageUrl.isNotEmpty()) {
            Picasso.get().load(imageUrl).into(imagenCliente)
        } else {
            // Si no hay imagen, cargar la imagen predeterminada
            imagenCliente.setImageResource(R.drawable.imagen_login)
        }

        // Establecer el listener del card para iniciar ProfileClientAdmin con los datos del cliente
        card.setOnClickListener {
            val intent = Intent(this, ProfileClientAdmin::class.java)
            intent.putExtra("nombre", nombre)
            intent.putExtra("imageUrl", imageUrl)
            intent.putExtra("rut", rut)
            intent.putExtra("correo", correo)
            intent.putExtra("telefono", telefono)
            intent.putExtra("clientId", clientId)
            startActivity(intent)
        }

        clientesContainer.addView(card)
    }

    // Función para volver al menú
    private fun irAlMenu() {
        val intent = Intent(this, MenuAdmin::class.java)
        startActivity(intent)
        finish()
    }
}
