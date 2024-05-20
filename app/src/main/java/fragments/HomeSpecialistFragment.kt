package fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.proyecto_fixit_final.Home
import com.example.proyecto_fixit_final.R

class HomeSpecialistFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.home, container, false)
        val buttonOpenProfile: Button = view.findViewById(R.id.btnOpenProfile)
        buttonOpenProfile.setOnClickListener {
            (activity as? Home)?.openProfile()
        }
        return view
    }
}
