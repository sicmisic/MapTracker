package com.example.maptracker

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.maptracker.domain.model.Location
import com.example.maptracker.presentation.ui.screens.ListScreen
import com.example.maptracker.presentation.viewmodel.LocationsUiState
import com.example.maptracker.ui.theme.MapTrackerTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun emptyState_displaysNoSavedLocationsMessage() {
        composeTestRule.setContent {
            MapTrackerTheme {
                ListScreen(
                    uiState = LocationsUiState(locations = emptyList(), isLoading = false),
                    searchQuery = "",
                    onSearchQueryChange = {},
                    onDeleteLocation = {},
                )
            }
        }

        composeTestRule.onNodeWithText("No saved locations").assertIsDisplayed()
    }

    @Test
    fun emptyStateWithQuery_displaysNoMatchMessage() {
        composeTestRule.setContent {
            MapTrackerTheme {
                ListScreen(
                    uiState = LocationsUiState(locations = emptyList(), isLoading = false),
                    searchQuery = "something",
                    onSearchQueryChange = {},
                    onDeleteLocation = {},
                )
            }
        }

        composeTestRule.onNodeWithText("No locations match your search").assertIsDisplayed()
    }

    @Test
    fun locationList_displaysAllTitles() {
        val locations = listOf(
            Location(id = 1, title = "Coffee Shop", note = "Best espresso", latitude = 0.0, longitude = 0.0, timestamp = 0),
            Location(id = 2, title = "City Park", note = "", latitude = 0.0, longitude = 0.0, timestamp = 0),
        )

        composeTestRule.setContent {
            MapTrackerTheme {
                ListScreen(
                    uiState = LocationsUiState(locations = locations, isLoading = false),
                    searchQuery = "",
                    onSearchQueryChange = {},
                    onDeleteLocation = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Coffee Shop").assertIsDisplayed()
        composeTestRule.onNodeWithText("City Park").assertIsDisplayed()
        composeTestRule.onNodeWithText("Best espresso").assertIsDisplayed()
    }

    @Test
    fun locationList_displaysHintToAddWhenEmpty() {
        composeTestRule.setContent {
            MapTrackerTheme {
                ListScreen(
                    uiState = LocationsUiState(locations = emptyList(), isLoading = false),
                    searchQuery = "",
                    onSearchQueryChange = {},
                    onDeleteLocation = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Long-press on the map to add a pin").assertIsDisplayed()
    }
}
