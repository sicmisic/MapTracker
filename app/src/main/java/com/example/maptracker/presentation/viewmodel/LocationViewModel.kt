package com.example.maptracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maptracker.domain.model.Category
import com.example.maptracker.domain.model.Location
import com.example.maptracker.domain.model.PinColor
import com.example.maptracker.domain.usecase.DeleteLocationUseCase
import com.example.maptracker.domain.usecase.GetAllCategoriesUseCase
import com.example.maptracker.domain.usecase.GetAllLocationsUseCase
import com.example.maptracker.domain.usecase.GetLocationByIdUseCase
import com.example.maptracker.domain.usecase.SaveCategoryUseCase
import com.example.maptracker.domain.usecase.SaveLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LocationsUiState(
    val locations: List<Location> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

data class AddEditUiState(
    val title: String = "",
    val note: String = "",
    val colorHex: String = PinColor.DEFAULT.hex,
    val selectedCategoryId: Long? = null,
    val editingLocationId: Long = 0L,
    val titleError: Boolean = false,
    val isSaving: Boolean = false,
)

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val getAllLocationsUseCase: GetAllLocationsUseCase,
    private val getLocationByIdUseCase: GetLocationByIdUseCase,
    private val saveLocationUseCase: SaveLocationUseCase,
    private val deleteLocationUseCase: DeleteLocationUseCase,
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val saveCategoryUseCase: SaveCategoryUseCase,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val locationsUiState: StateFlow<LocationsUiState> = combine(
        getAllLocationsUseCase(),
        _searchQuery,
    ) { locations, query ->
        val filtered = if (query.isBlank()) locations
        else locations.filter {
            it.title.contains(query, ignoreCase = true) ||
                it.note.contains(query, ignoreCase = true)
        }
        LocationsUiState(locations = filtered, isLoading = false)
    }
        .catch { e -> emit(LocationsUiState(isLoading = false, error = e.message)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = LocationsUiState(),
        )

    val categoriesState: StateFlow<List<Category>> = getAllCategoriesUseCase()
        .catch { emit(emptyList()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    private val _addEditUiState = MutableStateFlow(AddEditUiState())
    val addEditUiState: StateFlow<AddEditUiState> = _addEditUiState.asStateFlow()

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onTitleChange(title: String) {
        _addEditUiState.update { it.copy(title = title, titleError = false) }
    }

    fun onNoteChange(note: String) {
        _addEditUiState.update { it.copy(note = note) }
    }

    fun onColorChange(hex: String) {
        _addEditUiState.update { it.copy(colorHex = hex) }
    }

    fun onCategorySelect(categoryId: Long?) {
        _addEditUiState.update { state ->
            val category = categoryId?.let { id -> categoriesState.value.find { it.id == id } }
            state.copy(
                selectedCategoryId = categoryId,
                colorHex = category?.colorHex ?: state.colorHex,
            )
        }
    }

    fun loadLocationForEdit(locationId: Long) {
        viewModelScope.launch {
            getLocationByIdUseCase(locationId).first()?.let { location ->
                _addEditUiState.update {
                    AddEditUiState(
                        title = location.title,
                        note = location.note,
                        colorHex = location.colorHex,
                        selectedCategoryId = location.categoryId,
                        editingLocationId = location.id,
                    )
                }
            }
        }
    }

    fun saveLocation(lat: Double, lng: Double, onSuccess: () -> Unit) {
        val state = _addEditUiState.value
        if (state.title.isBlank()) {
            _addEditUiState.update { it.copy(titleError = true) }
            return
        }
        viewModelScope.launch {
            _addEditUiState.update { it.copy(isSaving = true) }
            saveLocationUseCase(
                Location(
                    id = state.editingLocationId,
                    title = state.title.trim(),
                    note = state.note.trim(),
                    latitude = lat,
                    longitude = lng,
                    colorHex = state.colorHex,
                    categoryId = state.selectedCategoryId,
                )
            )
            _addEditUiState.value = AddEditUiState()
            onSuccess()
        }
    }

    fun saveCategory(name: String, colorHex: String) {
        viewModelScope.launch {
            saveCategoryUseCase(Category(name = name, colorHex = colorHex))
        }
    }

    fun deleteLocation(location: Location) {
        viewModelScope.launch {
            deleteLocationUseCase(location)
        }
    }

    fun resetAddEditState() {
        _addEditUiState.value = AddEditUiState()
    }
}
