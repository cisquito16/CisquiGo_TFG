package com.example.cisquigo_tfg.ui.theme

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cisquigo_tfg.R

class RestauranteAdapter(private val restaurantes: List<Restaurante>) :
    RecyclerView.Adapter<RestauranteAdapter.RestauranteViewHolder>() {

    class RestauranteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.tvNombreRestaurante)
        val imagen: ImageView = view.findViewById(R.id.ivRestaurante)
        val rating: TextView = view.findViewById(R.id.tvRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestauranteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_restaurante, parent, false)
        return RestauranteViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestauranteViewHolder, position: Int) {
        val res = restaurantes[position]
        holder.nombre.text = res.name
        holder.rating.text = "⭐ ${res.rating}"

        Glide.with(holder.itemView.context)
            .load(res.image_url)
            .centerCrop()
            .into(holder.imagen)
    }

    override fun getItemCount() = restaurantes.size
}