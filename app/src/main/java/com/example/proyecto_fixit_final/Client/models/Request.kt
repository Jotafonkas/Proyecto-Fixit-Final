package com.example.proyecto_fixit_final.Client

// Clase que representa un servicio
data class Request(
    val id: String,
    val clienteId: String,
    var nombreCliente: String,
    val servicioId: String,
    var nombreServicio: String,
    var nombreEspecialista: String,
    val estado: String, // "pendiente", "aceptada", "rechazada"
    val descripcionServicio: String
)