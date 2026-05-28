package com.example.maptracker.presentation.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.maptracker.domain.model.Category
import com.example.maptracker.domain.model.Location
import com.example.maptracker.presentation.viewmodel.LocationsUiState
import com.example.maptracker.presentation.viewmodel.LocationViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
internal fun ListRoute(
    onNavigateToEditLocation: (locationId: Long, lat: Double, lng: Double) -> Unit,
    onNavigateToMap: (lat: Double, lng: Double) -> Unit,
    viewModel: LocationViewModel = hiltViewModel(),
) {
    val uiState by viewModel.locationsUiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val categories by viewModel.categoriesState.collectAsStateWithLifecycle()

    ListScreen(
        uiState = uiState,
        searchQuery = searchQuery,
        categories = categories,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onDeleteLocation = viewModel::deleteLocation,
        onNavigateToEdit = onNavigateToEditLocation,
        onNavigateToMap = onNavigateToMap,
    )
}

@Composable
internal fun ListScreen(
    uiState: LocationsUiState,
    searchQuery: String,
    categories: List<Category>,
    onSearchQueryChange: (String) -> Unit,
    onDeleteLocation: (Location) -> Unit,
    onNavigateToEdit: (locationId: Long, lat: Double, lng: Double) -> Unit,
    onNavigateToMap: (lat: Double, lng: Double) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text("Search locations…") },
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
            singleLine = true,
        )

        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Error: ${uiState.error}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp),
                    )
                }
            }

            uiState.locations.isEmpty() -> {
                EmptyState(
                    modifier = Modifier.fillMaxSize(),
                    hasSearchQuery = searchQuery.isNotBlank(),
                )
            }

            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(
                        items = uiState.locations,
                        key = { it.id },
                    ) { location ->
                        val category = categories.find { it.id == location.categoryId }
                        SwipeableLocationItem(
                            location = location,
                            category = category,
                            onDelete = { onDeleteLocation(location) },
                            onEdit = { onNavigateToEdit(location.id, location.latitude, location.longitude) },
                            onCardClick = { onNavigateToMap(location.latitude, location.longitude) },
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableLocationItem(
    location: Location,
    category: Category?,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onCardClick: () -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState()

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
            onDelete()
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            AnimatedVisibility(
                visible = dismissState.targetValue == SwipeToDismissBoxValue.EndToStart,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.errorContainer)
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.CenterEnd,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                    )
                }
            }
        },
        enableDismissFromStartToEnd = false,
    ) {
        LocationListItem(location = location, category = category, onEdit = onEdit, onCardClick = onCardClick)
    }
}

@Composable
private fun LocationListItem(
    location: Location,
    category: Category?,
    onEdit: () -> Unit,
    onCardClick: () -> Unit,
) {
    val pinColor = Color(android.graphics.Color.parseColor(location.colorHex))
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onCardClick,
        colors = CardDefaults.cardColors(),
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(pinColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.LocationOn,
                    contentDescription = null,
                    tint = pinColor,
                    modifier = Modifier.size(24.dp),
                )
            }
            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(1f),
            ) {
                Text(
                    text = location.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (location.note.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = location.note,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                if (category != null) {
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(android.graphics.Color.parseColor(category.colorHex))),
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${"%.4f".format(location.latitude)}, ${"%.4f".format(location.longitude)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                )
                Text(
                    text = SimpleDateFormat("MMM d, yyyy HH:mm", Locale.getDefault())
                        .format(Date(location.timestamp)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                )
            }
            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Rounded.Edit,
                    contentDescription = "Edit",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun EmptyState(
    hasSearchQuery: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Rounded.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.outlineVariant,
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = if (hasSearchQuery) "No locations match your search"
                else "No saved locations",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (!hasSearchQuery) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Long-press on the map to add a pin",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline,
                )
            }
        }
    }
}
