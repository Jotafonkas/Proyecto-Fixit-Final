package com.example.proyecto_fixit_final.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import com.example.proyecto_fixit_final.R
import com.example.proyecto_fixit_final.Specialist.CreateServicesSpecialist
import com.google.firebase.auth.FirebaseAuth

class ServicesFragment : Fragment() {

    private lateinit var uid: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_services, container, false)

        // Obtener el UID del usuario logeado
        uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        // Encuentra tus LinearLayouts y Buttons
        val linearLayout1: LinearLayout = view.findViewById(R.id.linearLayout1)
        val button1: Button = view.findViewById(R.id.button1)

        val linearLayout2: LinearLayout = view.findViewById(R.id.linearLayout2)
        val button2: Button = view.findViewById(R.id.button2)

        val linearLayout3: LinearLayout = view.findViewById(R.id.linearLayout3)
        val button3: Button = view.findViewById(R.id.button3)

        val linearLayout4: LinearLayout = view.findViewById(R.id.linearLayout4)
        val button4: Button = view.findViewById(R.id.button4)

        val linearLayout5: LinearLayout = view.findViewById(R.id.linearLayout5)
        val button5: Button = view.findViewById(R.id.button5)

        val linearLayout6: LinearLayout = view.findViewById(R.id.linearLayout6)
        val button6: Button = view.findViewById(R.id.button6)

        val linearLayout7: LinearLayout = view.findViewById(R.id.linearLayout7)
        val button7: Button = view.findViewById(R.id.button7)

        val linearLayout8: LinearLayout = view.findViewById(R.id.linearLayout8)
        val button8: Button = view.findViewById(R.id.button8)

        val linearLayout9: LinearLayout = view.findViewById(R.id.linearLayout9)
        val button9: Button = view.findViewById(R.id.button9)

        // Agrega listeners para los botones
        button1.setOnClickListener {
            val serviceName = button1.text.toString()
            redirectToServiceCreation(serviceName)
        }

        button2.setOnClickListener {
            val serviceName = button2.text.toString()
            redirectToServiceCreation(serviceName)
        }

        button3.setOnClickListener {
            val serviceName = button3.text.toString()
            redirectToServiceCreation(serviceName)
        }

        button4.setOnClickListener {
            val serviceName = button4.text.toString()
            redirectToServiceCreation(serviceName)
        }

        button5.setOnClickListener {
            val serviceName = button5.text.toString()
            redirectToServiceCreation(serviceName)
        }

        button6.setOnClickListener {
            val serviceName = button6.text.toString()
            redirectToServiceCreation(serviceName)
        }

        button7.setOnClickListener {
            val serviceName = button7.text.toString()
            redirectToServiceCreation(serviceName)
        }

        button8.setOnClickListener {
            val serviceName = button8.text.toString()
            redirectToServiceCreation(serviceName)
        }

        button9.setOnClickListener {
            val serviceName = button9.text.toString()
            redirectToServiceCreation(serviceName)
        }

        return view
    }

    private fun redirectToServiceCreation(serviceName: String) {
        val intent = Intent(activity, CreateServicesSpecialist::class.java)
        intent.putExtra("serviceName", serviceName)
        intent.putExtra("uid", uid) // Pasar el UID del usuario a la actividad de creación de servicios
        startActivity(intent)
    }

    companion object {
        @JvmStatic
        fun newInstance() = ServicesFragment()
    }
}
