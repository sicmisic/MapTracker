package com.example.maptracker.domain.usecase

import com.example.maptracker.domain.model.Location
import com.example.maptracker.domain.repository.LocationRepository
import javax.inject.Inject

class DeleteLocationUseCase @Inject constructor(
    private val repository: LocationRepository,
) {
    suspend operator fun invoke(location: Location) = repository.deleteLocation(location)
}
