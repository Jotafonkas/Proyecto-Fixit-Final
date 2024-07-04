package com.example.proyecto_fixit_final

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_fixit_final.Client.Request

class SpecialistRequestAdapter(
    private val solicitudes: List<Request>,
    private val onAcceptClick: (Request) -> Unit,
    private val onRejectClick: (Request) -> Unit

) : RecyclerView.Adapter<SpecialistRequestAdapter.SolicitudViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SolicitudViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.especialista_card_solicitud_servicio, parent, false)
        return SolicitudViewHolder(view)
    }

    override fun onBindViewHolder(holder: SolicitudViewHolder, position: Int) {
        holder.bind(solicitudes[position])
        val solicitud = solicitudes[position]
        holder.btnAceptar.setOnClickListener { onAcceptClick(solicitud) }
        holder.btnRechazar.setOnClickListener { onRejectClick(solicitud) }
    }

    override fun getItemCount(): Int = solicitudes.size

    inner class SolicitudViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNombreServicio: TextView = itemView.findViewById(R.id.tvNombreServicio)
        private val tvNombreCliente: TextView = itemView.findViewById(R.id.tvNombreCliente)
        private val tvEstado: TextView = itemView.findViewById(R.id.tvEstado)
        val btnAceptar: Button = itemView.findViewById(R.id.btnAceptar)
        val btnRechazar: Button = itemView.findViewById(R.id.btnRechazar)

        fun bind(solicitud: Request) {
            tvNombreServicio.text = solicitud.nombreServicio
            tvNombreCliente.text = solicitud.nombreCliente
            tvEstado.text = solicitud.estado

        }


    }
}
