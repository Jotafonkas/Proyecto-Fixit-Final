package com.example.proyecto_fixit_final.fragments

import android.annotation.SuppressLint
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
import com.example.proyecto_fixit_final.Specialist.CreateServicesSpecialist
import com.google.firebase.auth.FirebaseAuth

class ServicesFragment : Fragment() {

    private lateinit var uid: String

    // Configuramos la vista del fragmento
    @SuppressLint("DiscouragedApi") // Suprime advertencias de API desaconsejadas
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_services, container, false)

        // Obtenemos el UID del usuario logeado
        uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        // Configuramos las categorías
        for (i in 1..9) { // Iteramos sobre las categorías
            val linearLayoutId = resources.getIdentifier("linearLayout$i", "id", activity?.packageName)
            val imageButtonId = resources.getIdentifier("imageButton$i", "id", activity?.packageName)
            val textViewId = resources.getIdentifier("txtView$i", "id", activity?.packageName)

            // Configuramos la categoría actual con los IDs obtenidos
            setupCategory(view, linearLayoutId, imageButtonId, textViewId)
        }

        return view
    }

    // Configuramos una categoría con los IDs de los elementos de la vista
    private fun setupCategory(view: View, linearLayoutId: Int, imageButtonId: Int, textViewId: Int) {
        val linearLayout: LinearLayout = view.findViewById(linearLayoutId)
        val imageButton: ImageButton = view.findViewById(imageButtonId)
        val textView: TextView = view.findViewById(textViewId)

        // Configuramos el evento de clic para redirigir a la creación de servicios
        val onClickListener = View.OnClickListener {
            val serviceName = textView.text.toString()
            redirectToServiceCreation(serviceName)
        }

        // Configuramos los eventos de clic para los elementos de la categoría
        linearLayout.setOnClickListener(onClickListener)
        imageButton.setOnClickListener(onClickListener)
        textView.setOnClickListener(onClickListener)
    }

    // Redirigimos a la actividad de creación de servicios
    private fun redirectToServiceCreation(serviceName: String) {
        val intent = Intent(activity, CreateServicesSpecialist::class.java)
        intent.putExtra("serviceName", serviceName)
        intent.putExtra("uid", uid) // Pasar el UID del usuario a la actividad de creación de servicios
        startActivity(intent)
    }

    // Objeto de compañía para instanciar el fragmento
    companion object {
        @JvmStatic // Anotación para indicar que el método es estático
        fun newInstance() = ServicesFragment() // Devuelve una nueva instancia del fragmento
    }
}
