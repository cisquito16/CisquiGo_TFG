package com.example.cisquigo_tfg.ui.theme

data class YelpResponse(val businesses: List<Restaurante>)
data class Restaurante(
    val name: String,
    val image_url: String,
    val rating: Double
)