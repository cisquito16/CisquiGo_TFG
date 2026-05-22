package com.example.cisquigo_tfg.ui.theme

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cisquigo_tfg.R
import com.example.cisquigo_tfg.databinding.LoginFragmentBinding
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment(R.layout.login_fragment) {

    private var _binding: LoginFragmentBinding? = null
    private val binding get() = _binding!!

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = LoginFragmentBinding.bind(view)

        binding.btnEntrarLogin.setOnClickListener {
            val email = binding.etCorreoLogin.text.toString().trim()
            val pass = binding.etContrasenaLogin.text.toString().trim()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        findNavController().navigate(R.id.action_login_to_home)
                    } else {
                        val error = task.exception?.message ?: "Error al entrar"
                        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvEnlaceRegistro.setOnClickListener {
            findNavController().navigate(R.id.action_login_a_register)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}