package com.example.cisquigo_tfg.ui.theme

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
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

    // Tu Token de Yelp (Asegúrate de que sea el de la cuenta nueva)
    private val TOKEN = "rs8lJl2yU2i7umluO5hV2v8StB3updxFqbfFsV_5tM8LnIUBSVCikibwQzlR5pHdGhxSkkCJQT57ye2Q98X2GKjoJ9kto5wWcD-hdjp2-oDgmdKh9ezpdjJadqXbaXYx"


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = HomeFragmentBinding.bind(view)

        binding.bottomNavigation.setupWithNavController(findNavController())

        // 1. CONFIGURAR RECYCLERVIEW (Grid de 2 columnas)
        binding.rvRestaurantes.layoutManager = GridLayoutManager(requireContext(), 2)

        // 2. CARGAR DATOS
        obtenerNombreYDatos()
        cargarRestaurantes()
    }

    private fun obtenerNombreYDatos() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("usuarios").document(uid).get()
            .addOnSuccessListener { doc ->
                if (isAdded) {
                    val nombre = doc.getString("nombre") ?: "Gourmet"
                    binding.tvBienvenida.text = "¡Bienvenido, $nombre!"
                }
            }
            .addOnFailureListener {
                if (isAdded) binding.tvBienvenida.text = "¡Bienvenido!"
            }
    }

    private fun cargarRestaurantes() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.yelp.com/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(YelpService::class.java)
        val authHeader = "Bearer $TOKEN"

        service.buscarRestaurantes(authHeader, "restaurants", "Madrid, Spain")
            .enqueue(object : Callback<YelpResponse> {
                override fun onResponse(call: Call<YelpResponse>, response: Response<YelpResponse>) {
                    if (response.isSuccessful && isAdded) {
                        val lista = response.body()?.businesses ?: emptyList()
                        binding.rvRestaurantes.adapter = RestauranteAdapter(lista)
                    } else {
                        Log.e("YELP_ERROR", "Error: ${response.code()} - Probablemente Token caducado o mal pegado")
                    }
                }

                override fun onFailure(call: Call<YelpResponse>, t: Throwable) {
                    if (isAdded) {
                        Log.e("YELP_FAIL", "Fallo de red: ${t.message}")
                        Toast.makeText(context, "Sin conexión a internet", Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}