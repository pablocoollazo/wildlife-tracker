package com.pablocollazo.wildlifetracker

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        @Suppress("DEPRECATION")
        val sighting = intent.getSerializableExtra("sighting") as Sighting

        val dateFormatted = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(
            Date(
                sighting.date
            )
        )

        findViewById<ImageView>(R.id.imageViewPhoto).setImageURI(Uri.parse(sighting.photo))
        findViewById<TextView>(R.id.textSpecie).text = sighting.species
        findViewById<TextView>(R.id.textNotes).text = sighting.notes
        findViewById<TextView>(R.id.textViewLocation).text = "Lat: ${sighting.latitude}, Lon: ${sighting.longitude}"
        findViewById<TextView>(R.id.textDate).text = dateFormatted
        findViewById<TextView>(R.id.textFavourite).text = if (sighting.isFavourite) "⭐ Favourite" else "Not favourite"



    }
}