package com.pablocollazo.wildlifetracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fab = findViewById<FloatingActionButton>(R.id.fabNewSighting)
        fab.setOnClickListener {
            val intent = Intent(this, NewSightingActivity::class.java)
            startActivity(intent)
        }
    }
}