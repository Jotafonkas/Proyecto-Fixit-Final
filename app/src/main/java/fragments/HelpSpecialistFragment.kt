package fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.proyecto_fixit_final.R

class HelpSpecialistFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.vista_ayuda, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imgContactoLlamar = view.findViewById<ImageView>(R.id.imgcontactollamar)
        val imgContactoCorreo = view.findViewById<ImageView>(R.id.imgcontactocorreo)

        imgContactoLlamar.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:+56934709635")
            startActivity(intent)
        }

        imgContactoCorreo.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:fixit@gmail.com")
            startActivity(intent)
        }

    }

}