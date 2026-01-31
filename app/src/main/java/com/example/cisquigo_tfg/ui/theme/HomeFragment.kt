package com.example.cisquigo_tfg.ui.theme

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.cisquigo_tfg.R
import com.example.cisquigo_tfg.databinding.HomeFragmentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeFragment : Fragment(R.layout.home_fragment) {

    private var _binding: HomeFragmentBinding? = null
    private val binding get() = _binding!!

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    // Tu Token (sin la palabra Bearer delante, se la añadimos abajo)
    private val TOKEN = "6AThhspUS2G7DwhBkr9ofhybszPrf5KDTYwMfEWFf5ZIpXJ-Wd2y_WXj9SIGNdvZgQN3yVGqWpeyHZNdLZ1qq1xjQZ9Y400dRvg36iNbuvrazsCK8Cn42CqZFTZ-aXYx"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = HomeFragmentBinding.bind(view)

        // Diseño en 2 columnas para que sea más visual
        binding.rvRestaurantes.layoutManager = GridLayoutManager(requireContext(), 2)

        obtenerNombreYDatos()
    }

    private fun obtenerNombreYDatos() {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("usuarios").document(uid).get()
                .addOnSuccessListener { doc ->
                    val nombre = doc.getString("nombre") ?: "Gourmet"
                    binding.tvBienvenida.text = "Bienvenido, $nombre"
                }
                .addOnFailureListener {
                    binding.tvBienvenida.text = "Bienvenido!"
                }
        }
        cargarRestaurantes()
    }

    private fun cargarRestaurantes() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.yelp.com/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(YelpService::class.java)

        // SOLUCIÓN AL 401: Añadimos "Bearer " antes del token aquí directamente
        val authHeader = "Bearer $TOKEN"

        service.buscarRestaurantes(authHeader, "restaurants", "Madrid, Spain")
            .enqueue(object : Callback<YelpResponse> {
                override fun onResponse(call: Call<YelpResponse>, response: Response<YelpResponse>) {
                    if (response.isSuccessful) {
                        val lista = response.body()?.businesses ?: emptyList()
                        binding.rvRestaurantes.adapter = RestauranteAdapter(lista)
                        Log.d("YELP_OK", "Restaurantes cargados: ${lista.size}")
                    } else {
                        // Si vuelve a dar error, mira el Logcat con el filtro YELP_ERROR
                        val errorCuerpo = response.errorBody()?.string()
                        Log.e("YELP_ERROR", "Código: ${response.code()} - Mensaje: $errorCuerpo")
                        Toast.makeText(context, "Error Api ${response.code()}", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<YelpResponse>, t: Throwable) {
                    Log.e("YELP_FAIL", "Error de red: ${t.message}")
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}