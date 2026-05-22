package com.example.cisquigo_tfg.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cisquigo_tfg.R
import com.example.cisquigo_tfg.databinding.FragmentNotificacionesBinding
import com.example.cisquigo_tfg.modelos.Reserva // Revisa que esta sea tu ruta real
import com.example.cisquigo_tfg.ui.adapters.ReservasAdapter // Revisa que esta sea tu ruta real
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NotificacionesFragment : Fragment(R.layout.fragment_notificaciones) {

    private var _binding: FragmentNotificacionesBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentNotificacionesBinding.bind(view)

        binding.bottomNavigation.setupWithNavController(findNavController())

        // Configuración del RecyclerView
        binding.rvReservas.layoutManager = LinearLayoutManager(requireContext())

        cargarReservas()
    }

    private fun cargarReservas() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("reservas")
            .whereEqualTo("usuarioId", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Si hay un error de Firebase, aquí podrías loguearlo
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    // Convertimos los documentos de Firebase a objetos de nuestra clase Reserva
                    val lista = snapshot.toObjects(Reserva::class.java)

                    // Le pasamos la lista al Adapter que creamos antes
                    binding.rvReservas.adapter = ReservasAdapter(lista)

                    // Gestión de visibilidad (opcional):
                    // if (lista.isEmpty()) { ... mostrar texto vacío ... }
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}