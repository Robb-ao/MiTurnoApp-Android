package com.example.miturnoapp.data

// Para extraer los datos del JSON de la API
data class PublicHoliday(
    val date: String,       // Fecha en formato YYYY-MM-DD
    val localName: String,  // Nombre del dia festivo
    val name: String        // Nombre internacional
)
