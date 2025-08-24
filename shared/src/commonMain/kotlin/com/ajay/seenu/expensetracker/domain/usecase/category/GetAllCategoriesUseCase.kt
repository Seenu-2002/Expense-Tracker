package com.ajay.seenu.expensetracker.domain.usecase.category

import com.ajay.seenu.expensetracker.data.repository.CategoryRepository
import com.ajay.seenu.expensetracker.domain.model.Category
import com.ajay.seenu.expensetracker.domain.model.TransactionType


class GetAllCategoriesUseCase constructor(
    private val repository: CategoryRepository
) {

    suspend operator fun invoke(type: TransactionType): List<Category> {
        return repository.getCategories(
            type
        )
    }

}