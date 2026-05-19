package com.example.cisquigo_tfg.ui.theme

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cisquigo_tfg.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RestauranteAdapter(private val restaurantes: List<Restaurante>) :
    RecyclerView.Adapter<RestauranteAdapter.RestauranteViewHolder>() {

    class RestauranteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.tvNombreRestaurante)
        val imagen: ImageView = view.findViewById(R.id.ivRestaurante)
        val rating: TextView = view.findViewById(R.id.tvRating)
        val btnFav: ImageButton = view.findViewById(R.id.btnFavItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestauranteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_restaurante, parent, false)
        return RestauranteViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestauranteViewHolder, position: Int) {
        val res = restaurantes[position]
        val context = holder.itemView.context

        holder.nombre.text = res.name
        holder.rating.text = "⭐ ${res.rating}"

        Glide.with(context)
            .load(res.image_url)
            .into(holder.imagen)

        // --- LÓGICA FAVORITOS ---
        holder.btnFav.setOnClickListener {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                val db = FirebaseFirestore.getInstance()
                val favData = hashMapOf(
                    "id" to res.id,
                    "name" to res.name,
                    "image_url" to res.image_url,
                    "rating" to res.rating,
                    "usuarioId" to userId
                )

                db.collection("favoritos").document("${userId}_${res.id}")
                    .set(favData)
                    .addOnSuccessListener {
                        Toast.makeText(context, "${res.name} añadido a favoritos", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        // --- CLIC PARA IR AL DETALLE (Donde se hace la reserva) ---
        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetalleActivity::class.java).apply {
                putExtra("id", res.id)
                putExtra("nombre", res.name)
                putExtra("imagen", res.image_url)
                putExtra("rating", res.rating)
                // Convertimos la dirección a un String bonito para la reserva
                putExtra("direccion", res.location.display_address.joinToString(", "))
                putExtra("telefono", res.display_phone ?: "No disponible")
                putExtra("categoria", res.categories.firstOrNull()?.title ?: "Restaurante")
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = restaurantes.size
}