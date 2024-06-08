package com.example.proyecto_fixit_final.Specialist.modelos

// Clase que representa a un especialista
data class Specialist(
    val uid: String = "", // Añadido el campo para el UID del especialista
    val nombre: String = "",
    val imageUrl: String = "",
    val nombreServicio: String = "",
    val categoria: String = "",
    val descripcionServicio: String = "" // Añadido el campo para la descripción del servicio
)
