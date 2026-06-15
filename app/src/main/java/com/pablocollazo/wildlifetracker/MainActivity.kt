package com.pablocollazo.wildlifetracker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var database: SightingDatabase
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: SightingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fab = findViewById<FloatingActionButton>(R.id.fabNewSighting)
        fab.setOnClickListener {
            val intent = Intent(this, NewSightingActivity::class.java)
            startActivity(intent)
        }

        val fabMap = findViewById<FloatingActionButton>(R.id.fabMap)
        fabMap.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }

        database = Room.databaseBuilder(this, SightingDatabase::class.java, "sighting_db").build()
        recycler = findViewById(R.id.recyclerViewSightings)
        recycler.layoutManager = LinearLayoutManager(this)
    }

    override fun onResume() {
        super.onResume()
        loadSightings()
    }

    private fun loadSightings() {
        lifecycleScope.launch(Dispatchers.IO) {
            val sightings = database.sightingDao().getSightings()

            runOnUiThread {
                adapter = SightingAdapter(
                    sightings,
                    onLongPress = { sighting ->
                        lifecycleScope.launch(Dispatchers.IO) {
                            database.sightingDao().updateSighting(sighting.copy(isFavourite = !sighting.isFavourite))
                            runOnUiThread { loadSightings() }
                        }
                    },
                    onClick = { sighting ->
                        val intent = Intent(this@MainActivity, DetailActivity::class.java)
                        intent.putExtra("sighting", sighting)
                        startActivity(intent)
                    }
                )

                recycler.adapter = adapter

                val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = false

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val position = viewHolder.bindingAdapterPosition
                        val sighting = adapter.getSighting(position)

                        lifecycleScope.launch(Dispatchers.IO) {
                            database.sightingDao().deleteSighting(sighting)
                            runOnUiThread { loadSightings() }
                        }
                    }
                })

                itemTouchHelper.attachToRecyclerView(recycler)
            }
        }
    }
}