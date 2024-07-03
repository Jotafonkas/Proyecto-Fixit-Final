package com.example.proyecto_fixit_final.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
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
import com.google.firebase.firestore.FirebaseFirestore

class ServicesFragment : Fragment() {

    private lateinit var uid: String
    private lateinit var firestore: FirebaseFirestore

    // Configuramos la vista del fragmento
    @SuppressLint("DiscouragedApi") // Suprime advertencias de API desaconsejadas
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_services, container, false)

        // Obtenemos el UID del usuario logeado
        uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        firestore = FirebaseFirestore.getInstance()

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
            checkAuthorizationAndRedirect(textView.text.toString())
        }

        // Configuramos los eventos de clic para los elementos de la categoría
        linearLayout.setOnClickListener(onClickListener)
        imageButton.setOnClickListener(onClickListener)
        textView.setOnClickListener(onClickListener)
    }

    private fun checkAuthorizationAndRedirect(serviceName: String) {
        firestore.collection("especialistas").document(uid).get().addOnSuccessListener { document ->
            if (document.exists()) {
                val autorizado = document.getBoolean("autorizado") ?: false
                val motivoRechazo = document.getString("motivoRechazo")

                if (autorizado) {
                    val intent = Intent(activity, CreateServicesSpecialist::class.java)
                    intent.putExtra("serviceName", serviceName)
                    intent.putExtra("uid", uid)
                    startActivity(intent)
                } else {
                    showAuthorizationDialog(motivoRechazo)
                }
            } else {
                showAuthorizationDialog("No estás registrado como especialista.")
            }
        }.addOnFailureListener {
            showAuthorizationDialog("Error al verificar el estado de autorización.")
        }
    }

    private fun showAuthorizationDialog(motivo: String?) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Autorización Requerida")
        val message = if (motivo.isNullOrEmpty()) {
            "No estás autorizado para crear servicios. Por favor, contacta con nosotros en la sección de Ayuda."
        } else {
            "Autorización rechazada: $motivo"
        }
        builder.setMessage(message)
        builder.setPositiveButton("OK", null)
        val dialog = builder.create()
        dialog.show()
    }

    // Objeto de compañía para instanciar el fragmento
    companion object {
        @JvmStatic // Anotación para indicar que el método es estático
        fun newInstance() = ServicesFragment() // Devuelve una nueva instancia del fragmento
    }
}
