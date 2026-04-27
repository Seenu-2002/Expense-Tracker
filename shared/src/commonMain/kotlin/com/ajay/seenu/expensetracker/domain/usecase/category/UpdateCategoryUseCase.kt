package com.ajay.seenu.expensetracker.domain.usecase.category

import com.ajay.seenu.expensetracker.data.repository.CategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext


class UpdateCategoryUseCase constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(id: Long, label: String, res: Int, color: Long) {
        withContext(Dispatchers.IO) {
            val existingCategory = repository.getCategory(id)
            val duplicates = repository.searchCategories(label, existingCategory.type)
            if (duplicates.any { it.id != id }) {
                throw IllegalStateException("Category $label already present")
            }
            repository.updateCategory(id, label, res, color)
        }
    }
}