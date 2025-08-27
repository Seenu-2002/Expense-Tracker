package com.ajay.seenu.expensetracker.domain.usecase.category

import com.ajay.seenu.expensetracker.data.repository.TransactionRepository
import com.ajay.seenu.expensetracker.domain.model.Category

class ChangeCategoriesUseCase constructor(
    private val repository: TransactionRepository
) {

    suspend operator fun invoke(
        oldCategory: Category,
        newCategory: Category
    ) {
        repository.replaceCategoryInTransactions(
            oldCategory = oldCategory.id,
            newCategory = newCategory.id
        )
    }

}