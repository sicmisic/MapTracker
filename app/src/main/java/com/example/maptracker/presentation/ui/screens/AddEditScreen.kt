package com.example.maptracker.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.maptracker.domain.model.Category
import com.example.maptracker.domain.model.PinColor
import com.example.maptracker.presentation.viewmodel.AddEditUiState
import com.example.maptracker.presentation.viewmodel.LocationViewModel

@Composable
internal fun AddEditRoute(
    lat: Double,
    lng: Double,
    locationId: Long = 0L,
    onNavigateBack: () -> Unit,
    viewModel: LocationViewModel = hiltViewModel(),
) {
    val addEditUiState by viewModel.addEditUiState.collectAsStateWithLifecycle()
    val categories by viewModel.categoriesState.collectAsStateWithLifecycle()

    LaunchedEffect(locationId) {
        if (locationId != 0L) viewModel.loadLocationForEdit(locationId)
        else viewModel.resetAddEditState()
    }

    AddEditScreen(
        lat = lat,
        lng = lng,
        uiState = addEditUiState,
        categories = categories,
        onTitleChange = viewModel::onTitleChange,
        onNoteChange = viewModel::onNoteChange,
        onColorChange = viewModel::onColorChange,
        onCategorySelect = viewModel::onCategorySelect,
        onSaveCategory = viewModel::saveCategory,
        onSave = { viewModel.saveLocation(lat, lng, onSuccess = onNavigateBack) },
        onCancel = {
            viewModel.resetAddEditState()
            onNavigateBack()
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddEditScreen(
    lat: Double,
    lng: Double,
    uiState: AddEditUiState,
    categories: List<Category>,
    onTitleChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onColorChange: (String) -> Unit,
    onCategorySelect: (Long?) -> Unit,
    onSaveCategory: (name: String, colorHex: String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.editingLocationId == 0L) "Add Location" else "Edit Location") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(Modifier.height(8.dp))

            Text(
                text = "Coordinates",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${"%.6f".format(lat)},  ${"%.6f".format(lng)}",
                style = MaterialTheme.typography.bodyMedium,
            )

            Spacer(Modifier.height(20.dp))

            OutlinedTextField(
                value = uiState.title,
                onValueChange = onTitleChange,
                label = { Text("Title *") },
                isError = uiState.titleError,
                supportingText = if (uiState.titleError) {
                    { Text("Title is required") }
                } else null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.note,
                onValueChange = onNoteChange,
                label = { Text("Note") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4,
                maxLines = 6,
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Color",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(10.dp))
            ColorPicker(selectedHex = uiState.colorHex, onColorSelected = onColorChange)

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Category",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(8.dp))
            CategorySection(
                categories = categories,
                selectedCategoryId = uiState.selectedCategoryId,
                onCategorySelect = onCategorySelect,
                onCreateCategory = onSaveCategory,
            )

            Spacer(Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Cancel")
                }

                Spacer(Modifier.width(12.dp))

                Button(
                    onClick = onSave,
                    modifier = Modifier.weight(1f),
                    enabled = !uiState.isSaving,
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Text("Save")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ColorPicker(
    selectedHex: String,
    onColorSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        PinColor.entries.forEach { pinColor ->
            val isSelected = pinColor.hex == selectedHex
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(android.graphics.Color.parseColor(pinColor.hex)))
                    .then(
                        if (isSelected) {
                            Modifier.border(3.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                        } else {
                            Modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                        }
                    )
                    .clickable { onColorSelected(pinColor.hex) },
                contentAlignment = Alignment.Center,
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = pinColor.label,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun CategorySection(
    categories: List<Category>,
    selectedCategoryId: Long?,
    onCategorySelect: (Long?) -> Unit,
    onCreateCategory: (name: String, colorHex: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showCreateDialog by remember { mutableStateOf(false) }

    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        FilterChip(
            selected = selectedCategoryId == null,
            onClick = { onCategorySelect(null) },
            label = { Text("None") },
        )
        categories.forEach { category ->
            FilterChip(
                selected = selectedCategoryId == category.id,
                onClick = { onCategorySelect(category.id) },
                label = { Text(category.name) },
                leadingIcon = {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(android.graphics.Color.parseColor(category.colorHex))),
                    )
                },
            )
        }
        AssistChip(
            onClick = { showCreateDialog = true },
            label = { Text("New…") },
            leadingIcon = {
                Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            },
        )
    }

    if (showCreateDialog) {
        CreateCategoryDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { name, colorHex ->
                onCreateCategory(name, colorHex)
                showCreateDialog = false
            },
        )
    }
}

@Composable
private fun CreateCategoryDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, colorHex: String) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var colorHex by remember { mutableStateOf(PinColor.RED.hex) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Category") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = "Color",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                ColorPicker(selectedHex = colorHex, onColorSelected = { colorHex = it })
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name.trim(), colorHex) },
                enabled = name.isNotBlank(),
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}
