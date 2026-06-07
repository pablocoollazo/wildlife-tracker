package com.pablocollazo.wildlifetracker

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SightingDAO {

    @Insert
    fun insertSighting(sighting: Sighting)

    @Delete
    fun deleteSighting(sighting: Sighting)

    @Query("SELECT * FROM Sighting")
    fun getSightings(): List<Sighting>
}

