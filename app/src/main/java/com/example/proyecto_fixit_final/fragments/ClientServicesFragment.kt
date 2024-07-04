package com.example.proyecto_fixit_final.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.example.proyecto_fixit_final.R
import com.example.proyecto_fixit_final.Specialist.ClientSpecialistsByCategory
import com.google.firebase.auth.FirebaseAuth

class ClientServicesFragment : Fragment() {

    private lateinit var uid: String // Variable para almacenar el UID del usuario

    // Función para crear la vista del fragmento
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_services_cliente, container, false)

        // Obtenemos el UID del usuario logeado
        uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        // Configuramos categorías
        for (i in 1..9) {
            val linearLayoutId = resources.getIdentifier("linearLayout$i", "id", activity?.packageName)
            val imageButtonId = resources.getIdentifier("imageButton$i", "id", activity?.packageName)
            val textViewId = resources.getIdentifier("txtView$i", "id", activity?.packageName)

            // Configuramos las categorías para que al hacer click en ellas se muestren los especialistas de esa categoría
            setupCategory(view, linearLayoutId, imageButtonId, textViewId)
        }

        return view
    }

    // Función para configurar las categorías de servicios y especialistas en la vista
    private fun setupCategory(view: View, linearLayoutId: Int, imageButtonId: Int, textViewId: Int) {
        val linearLayout: LinearLayout = view.findViewById(linearLayoutId)
        val imageButton: ImageButton = view.findViewById(imageButtonId)
        val textView: TextView = view.findViewById(textViewId)

        // Función para redirigir a la vista de especialistas de la categoría seleccionada
        val onClickListener = View.OnClickListener {
            val categoryName = textView.text.toString()
            val intent = Intent(activity, ClientSpecialistsByCategory::class.java)
            intent.putExtra("categoryName", categoryName)
            startActivity(intent)
        }

        // Asignamos las funciones de redirección a las categorías
        linearLayout.setOnClickListener(onClickListener)
        imageButton.setOnClickListener(onClickListener)
        textView.setOnClickListener(onClickListener)
    }
}
