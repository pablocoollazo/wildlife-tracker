package com.pablocollazo.wildlifetracker

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Sighting(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val photo: String,
    val species: String,
    val notes: String,
    val latitude: Double,
    val longitude: Double,
    val date: Long,
    val isFavourite: Boolean,
) : Serializable