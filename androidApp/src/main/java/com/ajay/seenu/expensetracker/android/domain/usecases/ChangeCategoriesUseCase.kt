package com.ajay.seenu.expensetracker.android.domain.usecases

import com.ajay.seenu.expensetracker.android.data.TransactionRepository
import com.ajay.seenu.expensetracker.android.domain.data.Transaction
import javax.inject.Inject

class ChangeCategoriesUseCase @Inject constructor(
    private val repository: TransactionRepository
) {

    suspend operator fun invoke(
        oldCategory: Transaction.Category,
        newCategory: Transaction.Category
    ) {
        repository.replaceCategory(
            oldCategory = oldCategory.id,
            newCategory = newCategory.id
        )
    }

}