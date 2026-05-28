package com.example.maptracker.domain.usecase

import com.example.maptracker.domain.model.Location
import com.example.maptracker.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllLocationsUseCase @Inject constructor(
    private val repository: LocationRepository,
) {
    operator fun invoke(): Flow<List<Location>> = repository.getAllLocations()
}
