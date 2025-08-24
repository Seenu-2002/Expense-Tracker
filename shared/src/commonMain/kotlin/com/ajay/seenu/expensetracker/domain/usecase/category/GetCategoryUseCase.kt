package com.ajay.seenu.expensetracker.domain.usecase.category

import com.ajay.seenu.expensetracker.data.repository.CategoryRepository
import com.ajay.seenu.expensetracker.domain.model.Category

class GetCategoryUseCase constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(id: Long): Category {
        return repository.getCategory(id)
    }
}