package com.example.proyecto_fixit_final.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_fixit_final.R
import com.google.android.material.imageview.ShapeableImageView
import android.widget.TextView
import com.example.proyecto_fixit_final.Specialist.modelos.Specialist
import com.squareup.picasso.Picasso

// Adaptador para mostrar especialistas en un RecyclerView
class SpecialistAdapter(private val specialists: List<Specialist>) : RecyclerView.Adapter<SpecialistAdapter.SpecialistViewHolder>() {

    // Crea una vista para cada especialista en la lista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecialistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_specialist_client, parent, false)
        return SpecialistViewHolder(view)
    }

    // Vincula los datos de un especialista con la vista
    override fun onBindViewHolder(holder: SpecialistViewHolder, position: Int) {
        holder.bind(specialists[position])
    }

    // Devuelve la cantidad de especialistas en la lista
    override fun getItemCount() = specialists.size

    // Clase que representa una vista de un especialista
    class SpecialistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ShapeableImageView = itemView.findViewById(R.id.imagen_especialista)
        private val nameView: TextView = itemView.findViewById(R.id.ednombreEspecialista)
        private val serviceNameView: TextView = itemView.findViewById(R.id.edNombreServicio)
        private val categoryView: TextView = itemView.findViewById(R.id.edcategoria_especialista)

        // Vincula los datos de un especialista con la vista
        fun bind(specialist: Specialist) {
            nameView.text = specialist.nombre
            serviceNameView.text = specialist.nombreServicio
            categoryView.text = specialist.categoria
            if (specialist.imageUrl.isNotEmpty()) {
                Picasso.get().load(specialist.imageUrl).into(imageView)
            }
        }
    }
}
