package com.example.maptracker

import com.example.maptracker.domain.model.Location
import com.example.maptracker.domain.usecase.DeleteLocationUseCase
import com.example.maptracker.domain.usecase.GetAllLocationsUseCase
import com.example.maptracker.domain.usecase.GetLocationByIdUseCase
import com.example.maptracker.domain.usecase.SaveLocationUseCase
import com.example.maptracker.presentation.viewmodel.LocationViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LocationViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeRepository: FakeLocationRepository
    private lateinit var viewModel: LocationViewModel

    @Before
    fun setup() {
        fakeRepository = FakeLocationRepository()
        viewModel = LocationViewModel(
            getAllLocationsUseCase = GetAllLocationsUseCase(fakeRepository),
            saveLocationUseCase = SaveLocationUseCase(fakeRepository),
            deleteLocationUseCase = DeleteLocationUseCase(fakeRepository),
            getLocationByIdUseCase = GetLocationByIdUseCase(fakeRepository),
        )
    }

    @Test
    fun `initial state has isLoading true`() {
        assertTrue(viewModel.locationsUiState.value.isLoading)
    }

    @Test
    fun `state reflects repository locations`() = runTest {
        val locations = listOf(
            Location(id = 1, title = "Cafe", note = "", latitude = 0.0, longitude = 0.0, timestamp = 0),
        )
        fakeRepository.setLocations(locations)

        val state = viewModel.locationsUiState.first { !it.isLoading }
        assertEquals(locations, state.locations)
    }

    @Test
    fun `search filters locations by title`() = runTest {
        fakeRepository.setLocations(
            listOf(
                Location(id = 1, title = "Coffee Shop", note = "", latitude = 0.0, longitude = 0.0, timestamp = 0),
                Location(id = 2, title = "Bookstore", note = "", latitude = 0.0, longitude = 0.0, timestamp = 0),
            )
        )
        viewModel.onSearchQueryChange("coffee")

        val state = viewModel.locationsUiState.first { !it.isLoading }
        assertEquals(1, state.locations.size)
        assertEquals("Coffee Shop", state.locations.first().title)
    }

    @Test
    fun `search filters locations by note`() = runTest {
        fakeRepository.setLocations(
            listOf(
                Location(id = 1, title = "Place", note = "great pizza here", latitude = 0.0, longitude = 0.0, timestamp = 0),
                Location(id = 2, title = "Other", note = "nothing special", latitude = 0.0, longitude = 0.0, timestamp = 0),
            )
        )
        viewModel.onSearchQueryChange("pizza")

        val state = viewModel.locationsUiState.first { !it.isLoading }
        assertEquals(1, state.locations.size)
    }

    @Test
    fun `saveLocation sets titleError when title is blank`() = runTest {
        viewModel.onTitleChange("")
        viewModel.saveLocation(0.0, 0.0, onSuccess = {})

        assertTrue(viewModel.addEditUiState.value.titleError)
        assertTrue(fakeRepository.getAllLocations().first().isEmpty())
    }

    @Test
    fun `saveLocation persists location and resets form on success`() = runTest {
        var successCalled = false
        viewModel.onTitleChange("My Spot")
        viewModel.onNoteChange("Nice place")
        viewModel.saveLocation(48.8, 2.3, onSuccess = { successCalled = true })

        val saved = fakeRepository.getAllLocations().first()
        assertEquals(1, saved.size)
        assertEquals("My Spot", saved.first().title)
        assertTrue(successCalled)
        assertEquals("", viewModel.addEditUiState.value.title)
        assertFalse(viewModel.addEditUiState.value.titleError)
    }

    @Test
    fun `deleteLocation removes location from state`() = runTest {
        val location = Location(id = 1, title = "Gone", note = "", latitude = 0.0, longitude = 0.0, timestamp = 0)
        fakeRepository.setLocations(listOf(location))

        viewModel.deleteLocation(location)

        val state = viewModel.locationsUiState.first { !it.isLoading }
        assertTrue(state.locations.isEmpty())
    }

    @Test
    fun `resetAddEditState clears title and note`() {
        viewModel.onTitleChange("Something")
        viewModel.onNoteChange("A note")

        viewModel.resetAddEditState()

        assertEquals("", viewModel.addEditUiState.value.title)
        assertEquals("", viewModel.addEditUiState.value.note)
        assertFalse(viewModel.addEditUiState.value.titleError)
    }
}
