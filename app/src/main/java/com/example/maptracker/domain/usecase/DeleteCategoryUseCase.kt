package com.example.maptracker.domain.usecase

import com.example.maptracker.domain.model.Category
import com.example.maptracker.domain.repository.CategoryRepository
import javax.inject.Inject

class DeleteCategoryUseCase @Inject constructor(
    private val repository: CategoryRepository,
) {
    suspend operator fun invoke(category: Category) = repository.deleteCategory(category)
}
