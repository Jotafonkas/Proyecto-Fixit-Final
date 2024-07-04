package com.example.proyecto_fixit_final.Admin

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto_fixit_final.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.squareup.picasso.Picasso

class AdminListClients : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var clientesContainer: LinearLayout
    private lateinit var searchFilter: EditText
    private val clientesList = mutableListOf<Client>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_lista_clientes)

        clientesContainer = findViewById(R.id.clientes_container)
        searchFilter = findViewById(R.id.search_filter)

        // Buscar clientes desde Firestore
        buscarClientes()

        // Configura los botones para volver al menú
        val flechaVolverMenu: ImageView = findViewById(R.id.flechavolver_menu)
        val menu: TextView = findViewById(R.id.menu)

        flechaVolverMenu.setOnClickListener { irAlMenu() }
        menu.setOnClickListener { irAlMenu() }

        // Configurar filtro de búsqueda
        searchFilter.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filtrarClientes(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun buscarClientes() {
        db.collection("clientes")
            .get()
            .addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
                if (task.isSuccessful) {
                    clientesList.clear()
                    for (document in task.result) {
                        val nombre = document.getString("nombre")
                        val imageUrl = document.getString("imageUrl")
                        val clientId = document.id
                        val rut = document.getString("rut")
                        val correo = document.getString("correo")
                        val telefono = document.getString("telefono")
                        val cliente = Client(nombre, imageUrl, clientId, rut, correo, telefono)
                        if (!clientesList.contains(cliente)) {
                            clientesList.add(cliente)
                            addClientCard(cliente)
                        }
                    }
                } else {
                    Log.w("ViewClientsAdmin", "Error al obtener datos del cliente.", task.exception)
                }
            })
    }

    private fun addClientCard(cliente: Client) {
        val inflater = LayoutInflater.from(this)
        val card = inflater.inflate(R.layout.admin_card_lista_usuarios, clientesContainer, false)

        val nombreCliente: TextView = card.findViewById(R.id.nombreUsuario)
        val imagenCliente: ImageView = card.findViewById(R.id.imagen_usuario)

        nombreCliente.text = cliente.nombre

        if (cliente.imageUrl != null && cliente.imageUrl.isNotEmpty()) {
            Picasso.get().load(cliente.imageUrl).into(imagenCliente)
        } else {
            imagenCliente.setImageResource(R.drawable.imagen_login)
        }

        card.setOnClickListener {
            val intent = Intent(this, AdminProfileClient::class.java)
            intent.putExtra("nombre", cliente.nombre)
            intent.putExtra("imageUrl", cliente.imageUrl)
            intent.putExtra("rut", cliente.rut)
            intent.putExtra("correo", cliente.correo)
            intent.putExtra("telefono", cliente.telefono)
            intent.putExtra("clientId", cliente.clientId)
            startActivity(intent)
        }

        clientesContainer.addView(card)
    }

    private fun filtrarClientes(query: String) {
        clientesContainer.removeAllViews()
        val filteredList = clientesList.filter {
            it.nombre?.contains(query, true) == true
        }
        for (cliente in filteredList) {
            addClientCard(cliente)
        }
    }

    private fun irAlMenu() {
        val intent = Intent(this, AdminMenu::class.java)
        startActivity(intent)
        finish()
    }

    data class Client(
        val nombre: String?,
        val imageUrl: String?,
        val clientId: String,
        val rut: String?,
        val correo: String?,
        val telefono: String?
    )
}
