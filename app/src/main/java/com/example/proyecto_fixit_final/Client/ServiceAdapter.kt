package com.example.proyecto_fixit_final.Client

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_fixit_final.R
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso
import android.widget.TextView

class ServiceAdapter(private val services: List<Services>) : RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_servicios_solicitados, parent, false)
        return ServiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        holder.bind(services[position])
    }

    override fun getItemCount() = services.size

    inner class ServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ShapeableImageView = itemView.findViewById(R.id.imagen_especialistaH)
        private val nameView: TextView = itemView.findViewById(R.id.ednombreEspecialistaH)
        private val serviceNameView: TextView = itemView.findViewById(R.id.edNombreServicioH)
        private val valorView: TextView = itemView.findViewById(R.id.edvalor_especialistaH)

        fun bind(service: Services) {
            nameView.text = service.nombre
            serviceNameView.text = service.nombreServicio
            valorView.text = service.valor
            if (service.imageUrl.isNotEmpty()) {
                Picasso.get().load(service.imageUrl).into(imageView)
            } else {
                imageView.setImageResource(R.drawable.default_image) // Imagen por defecto si no hay URL
            }
        }
    }
}
