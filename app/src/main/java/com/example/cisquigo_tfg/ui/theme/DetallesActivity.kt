package com.example.cisquigo_tfg.ui.theme

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.cisquigo_tfg.databinding.ActivityDetallesBinding
import java.util.*

class DetalleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetallesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetallesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Recuperar datos del Intent
        val nombre = intent.getStringExtra("nombre")
        val imagen = intent.getStringExtra("imagen")
        val rating = intent.getDoubleExtra("rating", 0.0)
        val direccion = intent.getStringExtra("direccion")
        val categoria = intent.getStringExtra("categoria")

        // 2. Pintar datos en la UI
        binding.tvDetalleNombre.text = nombre
        binding.tvDetalleRating.text = "⭐ $rating"
        binding.tvDetalleDireccion.text = direccion
        binding.tvDetalleCategorias.text = categoria

        Glide.with(this).load(imagen).into(binding.ivDetalleImagen)

        // 3. Botón de Pedir Cita (Abre calendario)
        binding.btnPedirCita.setOnClickListener {
            val cal = Calendar.getInstance()
            val dpd = DatePickerDialog(this, { _, year, month, day ->
                val fecha = "$day/${month + 1}/$year"
                Toast.makeText(this, "Cita solicitada para $nombre el día $fecha", Toast.LENGTH_LONG).show()
                // Aquí podrías añadir la lógica de Firebase para guardar la cita
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
            dpd.show()
        }

        binding.btnVolver.setOnClickListener { finish() }
    }
}