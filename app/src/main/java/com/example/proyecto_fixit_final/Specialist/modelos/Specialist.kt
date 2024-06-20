package com.example.proyecto_fixit_final.Specialist.modelos

// Clase que representa a un especialista
data class Specialist(
    val uid: String = "",
    val nombre: String = "",
    val imageUrl: String = "",
    val ciudad: String = "",
    val nombreServicio: String = "",
    val categoria: String = "",
    val descripcionServicio: String = "",
    val precio: String = ""
)
