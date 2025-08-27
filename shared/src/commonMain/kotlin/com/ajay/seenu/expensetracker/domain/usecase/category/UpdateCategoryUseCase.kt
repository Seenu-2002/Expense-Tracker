package com.ajay.seenu.expensetracker.domain.usecase.category

import com.ajay.seenu.expensetracker.data.repository.CategoryRepository


class UpdateCategoryUseCase constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(id: Long, label: String, res: Int, color: Long) {
        repository.updateCategory(id, label, res, color)
    }
}