package com.example.maptracker.domain.model

data class Location(
    val id: Long = 0,
    val title: String,
    val note: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long = System.currentTimeMillis(),
)
