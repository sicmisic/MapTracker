package com.example.maptracker.domain.repository

import com.example.maptracker.domain.model.Location
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun getAllLocations(): Flow<List<Location>>
    fun getLocationById(id: Long): Flow<Location?>
    suspend fun saveLocation(location: Location)
    suspend fun deleteLocation(location: Location)
}
