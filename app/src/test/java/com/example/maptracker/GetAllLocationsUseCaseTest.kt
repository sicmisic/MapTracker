package com.example.maptracker

import com.example.maptracker.domain.model.Location
import com.example.maptracker.domain.usecase.GetAllLocationsUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetAllLocationsUseCaseTest {

    private lateinit var fakeRepository: FakeLocationRepository
    private lateinit var useCase: GetAllLocationsUseCase

    @Before
    fun setup() {
        fakeRepository = FakeLocationRepository()
        useCase = GetAllLocationsUseCase(fakeRepository)
    }

    @Test
    fun `returns empty list when repository has no locations`() = runTest {
        val result = useCase().first()
        assertTrue(result.isEmpty())
    }

    @Test
    fun `returns all locations from repository`() = runTest {
        val locations = listOf(
            Location(id = 1, title = "Coffee Shop", note = "Great coffee", latitude = 48.8, longitude = 2.3, timestamp = 1000),
            Location(id = 2, title = "Park", note = "", latitude = 48.9, longitude = 2.4, timestamp = 2000),
        )
        fakeRepository.setLocations(locations)

        val result = useCase().first()

        assertEquals(2, result.size)
        assertEquals(locations, result)
    }

    @Test
    fun `emits updated list when repository changes`() = runTest {
        val location = Location(id = 1, title = "Place", note = "", latitude = 0.0, longitude = 0.0, timestamp = 0)
        fakeRepository.setLocations(listOf(location))

        val firstResult = useCase().first()
        assertEquals(1, firstResult.size)

        fakeRepository.setLocations(emptyList())
        val secondResult = useCase().first()
        assertTrue(secondResult.isEmpty())
    }
}
