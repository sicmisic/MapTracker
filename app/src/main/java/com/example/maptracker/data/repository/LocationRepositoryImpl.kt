package com.example.maptracker.data.repository

import com.example.maptracker.data.local.LocationDao
import com.example.maptracker.data.local.entity.toDomain
import com.example.maptracker.data.local.entity.toEntity
import com.example.maptracker.domain.model.Location
import com.example.maptracker.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class LocationRepositoryImpl @Inject constructor(
    private val dao: LocationDao,
) : LocationRepository {

    override fun getAllLocations(): Flow<List<Location>> =
        dao.getAllLocations().map { entities -> entities.map { it.toDomain() } }

    override fun getLocationById(id: Long): Flow<Location?> =
        dao.getLocationById(id).map { it?.toDomain() }

    override suspend fun saveLocation(location: Location) =
        dao.insertLocation(location.toEntity())

    override suspend fun deleteLocation(location: Location) =
        dao.deleteLocation(location.toEntity())
}
