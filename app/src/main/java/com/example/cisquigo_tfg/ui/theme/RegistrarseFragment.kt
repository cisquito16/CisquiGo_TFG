package com.example.cisquigo_tfg.ui.theme

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cisquigo_tfg.R
import com.example.cisquigo_tfg.databinding.RegistrarseFragmentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegistrarseFragment : Fragment(R.layout.registrarse_fragment) {

    private var _binding: RegistrarseFragmentBinding? = null
    private val binding get() = _binding!!

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = RegistrarseFragmentBinding.bind(view)

        binding.btnFinalizeRegister.setOnClickListener {
            val nombre = binding.etFullName.text.toString().trim()
            val email = binding.etEmailRegister.text.toString().trim()
            val contrasena = binding.etPasswordRegister.text.toString().trim()
            val tarjeta = binding.etCardNumber.text.toString().trim()

            // Validación básica
            if (nombre.isNotEmpty() && email.isNotEmpty() && contrasena.length >= 6 && tarjeta.length == 16) {
                registrarUsuario(email, contrasena, nombre, tarjeta)
            } else {
                val mensaje = if (contrasena.length < 6) "La contraseña debe tener al menos 6 caracteres"
                else if (tarjeta.length != 16) "La tarjeta debe tener 16 dígitos"
                else "Por favor, completa todos los campos"
                Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registrarUsuario(email: String, pass: String, nombre: String, tarjeta: String) {
        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid
                val userMap = hashMapOf(
                    "nombre" to nombre,
                    "email" to email,
                    "tarjeta" to tarjeta,
                    "uid" to userId
                )

                if (userId != null) {
                    db.collection("usuarios").document(userId).set(userMap)
                        .addOnSuccessListener {
                            // Verificación de seguridad para evitar crashes si el fragmento se cerró antes de terminar
                            if (isAdded) {
                                Toast.makeText(requireContext(), "Registro completado con éxito", Toast.LENGTH_SHORT).show()
                                findNavController().popBackStack()
                            }
                        }
                        .addOnFailureListener { e ->
                            if (isAdded) {
                                Toast.makeText(requireContext(), "Error en la base de datos: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            } else {
                if (isAdded) {
                    // Aquí el mensaje de error de Firebase (task.exception) suele venir en inglés por defecto de la API
                    Toast.makeText(requireContext(), "Error al registrar: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}