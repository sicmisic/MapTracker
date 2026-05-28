package com.example.maptracker

import com.example.maptracker.domain.model.Location
import com.example.maptracker.domain.usecase.SaveLocationUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SaveLocationUseCaseTest {

    private lateinit var fakeRepository: FakeLocationRepository
    private lateinit var useCase: SaveLocationUseCase

    @Before
    fun setup() {
        fakeRepository = FakeLocationRepository()
        useCase = SaveLocationUseCase(fakeRepository)
    }

    @Test
    fun `saves location to repository`() = runTest {
        val location = Location(title = "New Place", note = "A note", latitude = 48.8, longitude = 2.3)

        useCase(location)

        val saved = fakeRepository.getAllLocations().first()
        assertEquals(1, saved.size)
        assertEquals("New Place", saved.first().title)
        assertEquals("A note", saved.first().note)
    }

    @Test
    fun `saves multiple locations`() = runTest {
        useCase(Location(title = "Place 1", note = "", latitude = 1.0, longitude = 1.0))
        useCase(Location(title = "Place 2", note = "", latitude = 2.0, longitude = 2.0))

        val saved = fakeRepository.getAllLocations().first()
        assertEquals(2, saved.size)
    }

    @Test
    fun `auto-assigns id when id is zero`() = runTest {
        useCase(Location(id = 0, title = "New", note = "", latitude = 0.0, longitude = 0.0))

        val saved = fakeRepository.getAllLocations().first()
        assertEquals(1L, saved.first().id)
    }
}
