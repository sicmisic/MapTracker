package com.example.maptracker.data.repository

import com.example.maptracker.data.local.CategoryDao
import com.example.maptracker.data.local.entity.toDomain
import com.example.maptracker.data.local.entity.toEntity
import com.example.maptracker.domain.model.Category
import com.example.maptracker.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class CategoryRepositoryImpl @Inject constructor(
    private val dao: CategoryDao,
) : CategoryRepository {

    override fun getAllCategories(): Flow<List<Category>> =
        dao.getAllCategories().map { entities -> entities.map { it.toDomain() } }

    override suspend fun saveCategory(category: Category) =
        dao.insertCategory(category.toEntity())

    override suspend fun deleteCategory(category: Category) =
        dao.deleteCategory(category.toEntity())
}
