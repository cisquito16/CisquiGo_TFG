package com.example.cisquigo_tfg.ui.theme

data class YelpResponse(
    val businesses: List<Restaurante> = emptyList()
)

data class Restaurante(
    val id: String = "",
    val name: String = "",
    val image_url: String = "",
    val rating: Double = 0.0,
    val display_phone: String? = null,
    val categories: List<Categoria> = emptyList(),
    val location: LocationData = LocationData(),
    val usuarioId: String = ""
)

data class Categoria(
    val title: String = ""
)

data class LocationData(
    val display_address: List<String> = emptyList()
)