package com.pablocollazo.wildlifetracker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var database: SightingDatabase
    // Map associates each Marker with his Sighting
    private val markerSightingMap = mutableMapOf<Marker, Sighting>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        database = Room.databaseBuilder(
            this, SightingDatabase::class.java, "sighting_db"
        ).build()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        lifecycleScope.launch(Dispatchers.IO) {
            val sightings = database.sightingDao().getSightings()

            runOnUiThread {
                if (sightings.isEmpty()) return@runOnUiThread

                val boundsBuilder = LatLngBounds.Builder()

                for (sighting in sightings) {
                    val position = LatLng(sighting.latitude, sighting.longitude)
                    val marker = googleMap.addMarker(
                        MarkerOptions()
                            .position(position)
                            .title(sighting.species)
                            .snippet(sighting.notes)
                    )
                    // Save association marker → sighting
                    if (marker != null) {
                        markerSightingMap[marker] = sighting
                    }
                    boundsBuilder.include(position)
                }

                val bounds = boundsBuilder.build()
                googleMap.moveCamera(
                    CameraUpdateFactory.newLatLngBounds(bounds, 150)
                )

                // Clicking on marker opens DetailActivity
                googleMap.setOnMarkerClickListener { marker ->
                    val sighting = markerSightingMap[marker]
                    if (sighting != null) {
                        val intent = Intent(this@MapActivity, DetailActivity::class.java)
                        intent.putExtra("sighting", sighting)
                        startActivity(intent)
                    }
                    true
                }
            }
        }
    }
}