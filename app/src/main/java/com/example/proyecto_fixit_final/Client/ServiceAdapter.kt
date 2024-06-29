package com.example.proyecto_fixit_final.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_fixit_final.R
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso
import android.widget.TextView

class ServiceAdapter(private val services: MutableList<com.example.proyecto_fixit_final.modelos.Services>) : RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_servicio, parent, false)
        return ServiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        holder.bind(services[position])
    }

    override fun getItemCount() = services.size

    inner class ServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ShapeableImageView = itemView.findViewById(R.id.imagen_servicio)
        private val nameView: TextView = itemView.findViewById(R.id.edNombreServicio)
        private val descriptionView: TextView = itemView.findViewById(R.id.edDescripcionServicio)
        private val priceView: TextView = itemView.findViewById(R.id.edPrecio)
        private val estadoView: TextView = itemView.findViewById(R.id.estadoservicio)

        fun bind(service: com.example.proyecto_fixit_final.modelos.Services) {
            nameView.text = service.nombreServicio
            descriptionView.text = service.descripcionServicio
            priceView.text = service.precio
            estadoView.text = service.estado
            if (service.imageUrl.isNotEmpty()) {
                Picasso.get().load(service.imageUrl).into(imageView)
            } else {
                imageView.setImageResource(R.drawable.default_image) // Imagen por defecto si no hay URL
            }
        }
    }
}
