package com.example.cisquigo_tfg.ui.theme

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.cisquigo_tfg.databinding.ActivityDetallesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class DetalleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetallesBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetallesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Recuperar datos del Intent
        val nombre = intent.getStringExtra("nombre") ?: "Restaurante"
        val imagen = intent.getStringExtra("imagen")
        val rating = intent.getDoubleExtra("rating", 0.0)
        val direccion = intent.getStringExtra("direccion") ?: "Dirección no disponible"
        val categoria = intent.getStringExtra("categoria")

        // 2. Pintar datos en la UI
        binding.tvDetalleNombre.text = nombre
        binding.tvDetalleRating.text = "⭐ $rating"
        binding.tvDetalleDireccion.text = direccion
        binding.tvDetalleCategorias.text = categoria

        Glide.with(this).load(imagen).into(binding.ivDetalleImagen)

        // 3. Botón de Pedir Cita (Abre calendario y guarda en Firebase)
        binding.btnPedirCita.setOnClickListener {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                val cal = Calendar.getInstance()
                val dpd = DatePickerDialog(this, { _, year, month, day ->
                    val fechaSeleccionada = "$day/${month + 1}/$year"

                    // LLAMADA A FIREBASE PARA GUARDAR
                    guardarReserva(nombre, direccion, fechaSeleccionada, userId)

                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                dpd.show()
            } else {
                Toast.makeText(this, "Debes iniciar sesión para reservar", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnVolver.setOnClickListener { finish() }
    }

    private fun guardarReserva(nombre: String, direccion: String, fecha: String, userId: String) {
        val reservaData = hashMapOf(
            "restauranteNombre" to nombre,
            "direccion" to direccion,
            "fecha" to fecha,
            "usuarioId" to userId
        )

        db.collection("reservas")
            .add(reservaData)
            .addOnSuccessListener {
                Toast.makeText(this, "Reserva confirmada en $nombre para el $fecha", Toast.LENGTH_LONG).show()
                // Opcional: Cerrar la actividad para volver al Home tras reservar
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al guardar la reserva: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}