package com.pablocollazo.wildlifetracker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
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

        database = Room.databaseBuilder(this, SightingDatabase::class.java, "sighting_db").build()
        recycler = findViewById(R.id.recyclerViewSightings)
        recycler.layoutManager = LinearLayoutManager(this)
    }

    override fun onResume() {
        super.onResume()
        loadSightings()
    }

    private fun loadSightings() {
        lifecycleScope.launch(Dispatchers.IO){
            val sightings = database.sightingDao().getSightings()
            runOnUiThread {
                adapter = SightingAdapter(sightings)
                recycler.adapter = adapter
            }
        }
    }
}