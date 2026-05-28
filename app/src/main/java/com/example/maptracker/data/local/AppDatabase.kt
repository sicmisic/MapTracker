package com.example.maptracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.maptracker.data.local.entity.CategoryEntity
import com.example.maptracker.data.local.entity.LocationEntity

@Database(
    entities = [LocationEntity::class, CategoryEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        const val DATABASE_NAME = "map_tracker.db"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE locations ADD COLUMN colorHex TEXT NOT NULL DEFAULT '#F44336'")
                db.execSQL("ALTER TABLE locations ADD COLUMN categoryId INTEGER DEFAULT NULL")
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS categories (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        colorHex TEXT NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }
    }
}
