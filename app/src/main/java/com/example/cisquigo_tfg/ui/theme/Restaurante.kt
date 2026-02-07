package com.example.cisquigo_tfg.ui.theme

data class YelpResponse(
    val businesses: List<Restaurante>
)

data class Restaurante(
    val id: String,
    val name: String,
    val image_url: String,
    val rating: Double,
    val display_phone: String?,
    val categories: List<Categoria>,
    val location: LocationData
)

data class Categoria(
    val title: String
)

data class LocationData(
    val display_address: List<String>
)