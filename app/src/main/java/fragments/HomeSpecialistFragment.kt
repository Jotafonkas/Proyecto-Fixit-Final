package fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.proyecto_fixit_final.Home
import com.example.proyecto_fixit_final.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class HomeSpecialistFragment : Fragment() {

    private lateinit var headerTextView: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.home, container, false)

        headerTextView = view.findViewById(R.id.header)
        auth = FirebaseAuth.getInstance()
        firestore = Firebase.firestore
        val buttonOpenProfile: Button = view.findViewById(R.id.btnOpenProfile)
        val user = auth.currentUser
        if (user != null) {
            fetchAndDisplayUserName(user.uid)
        }

        buttonOpenProfile.setOnClickListener {
            (activity as? Home)?.openProfile()
        }
        return view
    }

    private fun fetchAndDisplayUserName(uid: String) {
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val nombre = document.getString("nombre")
                    headerTextView.text = "Bienvenid@, $nombre"
                } else {
                    Toast.makeText(context, "No se encontró el usuario.", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al obtener el usuario: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}