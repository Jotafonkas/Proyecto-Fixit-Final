package com.example.proyecto_fixit_final.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_fixit_final.R
import com.example.proyecto_fixit_final.Specialist.DetailServiceClient
import com.example.proyecto_fixit_final.Specialist.modelos.Specialist
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso
import android.widget.TextView

// Adaptador para mostrar especialistas en un RecyclerView
class SpecialistAdapter(private val specialists: List<Specialist>) : RecyclerView.Adapter<SpecialistAdapter.SpecialistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecialistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_specialist_client, parent, false)
        return SpecialistViewHolder(view)
    }

    override fun onBindViewHolder(holder: SpecialistViewHolder, position: Int) {
        holder.bind(specialists[position])
    }

    override fun getItemCount() = specialists.size

    // Clase que representa una vista de un especialista
    inner class SpecialistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ShapeableImageView = itemView.findViewById(R.id.imagen_especialista)
        private val nameView: TextView = itemView.findViewById(R.id.ednombreEspecialista)
        private val serviceNameView: TextView = itemView.findViewById(R.id.edNombreServicio)
        private val cityView: TextView = itemView.findViewById(R.id.edCiudadServicio)
        private val priceView: TextView = itemView.findViewById(R.id.edprecio_servicio)
        private val cardView: MaterialCardView = itemView.findViewById(R.id.card_specialist)

        // Vincula los datos de un especialista con la vista
        fun bind(specialist: Specialist) {
            nameView.text = specialist.nombre
            serviceNameView.text = specialist.nombreServicio
            cityView.text = specialist.ciudad
            priceView.text = specialist.precio
            if (specialist.imageUrl.isNotEmpty()) {
                Picasso.get().load(specialist.imageUrl).into(imageView)
            }

            cardView.setOnClickListener {
                val context: Context = itemView.context
                val intent = Intent(context, DetailServiceClient::class.java).apply {
                    putExtra("uid", specialist.uid)
                    putExtra("nombre", specialist.nombre)
                    putExtra("nombreServicio", specialist.nombreServicio)
                    putExtra("categoria", specialist.categoria)
                    putExtra("ciudad", specialist.ciudad)
                    putExtra("precio", specialist.precio)
                    putExtra("imageUrl", specialist.imageUrl)
                }
                context.startActivity(intent)
            }
        }
    }
}
