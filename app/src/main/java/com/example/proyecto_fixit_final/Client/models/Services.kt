package com.example.proyecto_fixit_final.Client

// Clase que representa un servicio
data class Services(
    val uid: String = "", // Añadido el campo para el UID del especialista
    val nombre: String = "",
    val imageUrl: String = "",
    val nombreServicio: String = "",
    val categoria: String = "",
    val descripcionServicio: String = "", // Añadido el campo para la descripción del servicio
    val valor: String = ""
) {
}