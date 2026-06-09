package com.pablocollazo.wildlifetracker

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SightingAdapter(private val sightings: List<Sighting>) : RecyclerView.Adapter<SightingAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sighting, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val sighting = sightings[position]
        holder.textSpecies.text = sighting.species
        val dateFormatted = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(
            Date(
                sighting.date
            )
        )
        holder.textDate.text = dateFormatted
        holder.imageView.setImageURI(Uri.parse(sighting.photo))

    }

    override fun getItemCount(): Int {
        return sightings.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val imageView = itemView.findViewById<ImageView>(R.id.imageViewPhoto)
        val textSpecies = itemView.findViewById<TextView>(R.id.textSpecie)
        val textDate = itemView.findViewById<TextView>(R.id.textDate)
    }
}