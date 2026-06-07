package com.pablocollazo.wildlifetracker

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Sighting::class], version = 1)
abstract class SightingDatabase: RoomDatabase() {
    abstract fun sightingDao(): SightingDAO
}