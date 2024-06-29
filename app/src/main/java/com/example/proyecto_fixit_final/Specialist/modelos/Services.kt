package com.example.proyecto_fixit_final.Specialist.modelos

data class Services(
    val uid: String = "",
    val nombre: String = "",
    val imageUrl: String = "",
    val nombreServicio: String = "",
    val categoria: String = "",
    val descripcionServicio: String = "",
    val precio: String = "",
    val nombreEspecialista: String = "",
    val imagenUrl: String = "",
    val estado: String = "Pendiente"
)
