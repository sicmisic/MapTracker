package com.example.maptracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.maptracker.data.local.entity.LocationEntity

@Database(
    entities = [LocationEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao

    companion object {
        const val DATABASE_NAME = "map_tracker.db"
    }
}
