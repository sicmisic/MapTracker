package com.example.maptracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.maptracker.domain.model.Location
import com.example.maptracker.domain.model.PinColor

@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val note: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val colorHex: String = PinColor.DEFAULT.hex,
    val categoryId: Long? = null,
)

fun LocationEntity.toDomain() = Location(
    id = id,
    title = title,
    note = note,
    latitude = latitude,
    longitude = longitude,
    timestamp = timestamp,
    colorHex = colorHex,
    categoryId = categoryId,
)

fun Location.toEntity() = LocationEntity(
    id = id,
    title = title,
    note = note,
    latitude = latitude,
    longitude = longitude,
    timestamp = timestamp,
    colorHex = colorHex,
    categoryId = categoryId,
)
