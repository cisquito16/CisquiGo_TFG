package com.example.cisquigo_tfg.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.cisquigo_tfg.R
import com.example.cisquigo_tfg.databinding.FavoritoFragmentBinding
import com.example.cisquigo_tfg.ui.theme.Restaurante
import com.example.cisquigo_tfg.ui.theme.RestauranteAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FavoritosFragment : Fragment(R.layout.favorito_fragment) {

    private var _binding: FavoritoFragmentBinding? = null
    private val binding get() = _binding!!
    private val listaFavoritos = mutableListOf<Restaurante>()
    private lateinit var adapter: RestauranteAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FavoritoFragmentBinding.bind(view)

        binding.bottomNavigation.setupWithNavController(findNavController())

        // Configuración Grid 2 columnas
        adapter = RestauranteAdapter(listaFavoritos)
        binding.rvFavoritos.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = this@FavoritosFragment.adapter
        }

        escucharFavoritos()
    }

    private fun escucharFavoritos() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance().collection("favoritos")
            .whereEqualTo("usuarioId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                listaFavoritos.clear()
                snapshot?.documents?.forEach { doc ->
                    doc.toObject(Restaurante::class.java)?.let { listaFavoritos.add(it) }
                }

                adapter.notifyDataSetChanged()

                // Gestionar visibilidad de pantalla vacía
                if (listaFavoritos.isEmpty()) {
                    binding.lytVacio.visibility = View.VISIBLE
                    binding.rvFavoritos.visibility = View.GONE
                } else {
                    binding.lytVacio.visibility = View.GONE
                    binding.rvFavoritos.visibility = View.VISIBLE
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}