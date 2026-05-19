package com.example.cisquigo_tfg.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.cisquigo_tfg.R
import com.example.cisquigo_tfg.modelos.Reserva
import com.google.firebase.firestore.FirebaseFirestore

class ReservasAdapter(private val lista: List<Reserva>) :
    RecyclerView.Adapter<ReservasAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.tvNombreRestaurante)
        val fecha: TextView = view.findViewById(R.id.tvFechaReserva)
        val direccion: TextView = view.findViewById(R.id.tvDireccionReserva)
        val btnBorrar: Button = view.findViewById(R.id.btnBorrarReserva)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reserva, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reserva = lista[position]
        holder.nombre.text = reserva.restauranteNombre
        holder.fecha.text = reserva.fecha
        holder.direccion.text = reserva.direccion

        holder.btnBorrar.setOnClickListener {
            val db = FirebaseFirestore.getInstance()

            // Buscamos la reserva en Firebase por sus datos para borrarla
            db.collection("reservas")
                .whereEqualTo("restauranteNombre", reserva.restauranteNombre)
                .whereEqualTo("fecha", reserva.fecha)
                .whereEqualTo("usuarioId", reserva.usuarioId)
                .get()
                .addOnSuccessListener { snapshot ->
                    for (doc in snapshot) {
                        db.collection("reservas").document(doc.id).delete()
                            .addOnSuccessListener {
                                Toast.makeText(holder.itemView.context, "Reserva cancelada", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
        }
    }

    override fun getItemCount() = lista.size
}