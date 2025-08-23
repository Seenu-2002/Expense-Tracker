package com.ajay.seenu.expensetracker.domain.usecase.category

import com.ajay.seenu.expensetracker.data.repository.CategoryRepository

class DeleteCategoryUseCase constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(id: Long) {
        repository.deleteCategory(id)
    }
}