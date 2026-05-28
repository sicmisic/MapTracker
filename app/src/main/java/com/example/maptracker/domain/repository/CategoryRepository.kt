package com.example.maptracker.domain.repository

import com.example.maptracker.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getAllCategories(): Flow<List<Category>>
    suspend fun saveCategory(category: Category)
    suspend fun deleteCategory(category: Category)
}
