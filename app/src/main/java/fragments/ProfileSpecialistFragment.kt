package fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.proyecto_fixit_final.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class ProfileSpecialistFragment : Fragment() {

    private lateinit var edCorreo: EditText
    private lateinit var edRut: EditText
    private lateinit var edNombre: EditText
    private lateinit var edProfesion: EditText
    private lateinit var edTelefono: EditText
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.perfil_especialista, container, false)
        edCorreo = view.findViewById(R.id.edCorreo)
        edRut = view.findViewById(R.id.edRut)
        edNombre = view.findViewById(R.id.edNombre)
        edProfesion = view.findViewById(R.id.edEspecialidad)
        edTelefono = view.findViewById(R.id.edTelefono)

        auth = FirebaseAuth.getInstance()
        firestore = Firebase.firestore

        val user = auth.currentUser
        if (user != null) {
            // Recuperar y mostrar el nombre del usuario desde Firestore
            fetchAndDisplayData(user.uid)
        }



        return view
    }

    private fun fetchAndDisplayData(uid: String) {
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val nombre = document.getString("nombre")
                    val correo = document.getString("correo")
                    val rut = document.getString("rut")
                    val profesion = document.getString("profesion")
                    val telefono = document.getString("telefono")
                    edNombre.setText(nombre)
                    edCorreo.setText(correo)
                    edRut.setText(rut)
                    edProfesion.setText(profesion)
                    edTelefono.setText(telefono)
                }
            }
    }

}
