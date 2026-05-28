package com.example.maptracker

import com.example.maptracker.domain.model.Location
import com.example.maptracker.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeLocationRepository : LocationRepository {

    private val _locations = MutableStateFlow<List<Location>>(emptyList())

    override fun getAllLocations(): Flow<List<Location>> = _locations

    override fun getLocationById(id: Long): Flow<Location?> =
        _locations.map { list -> list.find { it.id == id } }

    override suspend fun saveLocation(location: Location) {
        val withId = if (location.id == 0L) {
            location.copy(id = (_locations.value.maxOfOrNull { it.id } ?: 0L) + 1L)
        } else {
            location
        }
        _locations.update { list -> list.filter { it.id != withId.id } + withId }
    }

    override suspend fun deleteLocation(location: Location) {
        _locations.update { list -> list.filter { it.id != location.id } }
    }

    fun setLocations(locations: List<Location>) {
        _locations.value = locations
    }
}
