package com.example.cisquigo_tfg.ui.theme

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface YelpService {
    @GET("businesses/search")
    fun buscarRestaurantes(
        @Header("Authorization") authHeader: String,
        @Query("term") term: String,
        @Query("location") location: String
    ): Call<YelpResponse>
}