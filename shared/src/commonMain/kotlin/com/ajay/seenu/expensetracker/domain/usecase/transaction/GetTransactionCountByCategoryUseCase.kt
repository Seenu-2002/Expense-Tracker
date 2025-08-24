package com.ajay.seenu.expensetracker.domain.usecase.transaction

import com.ajay.seenu.expensetracker.data.repository.TransactionRepository
import com.ajay.seenu.expensetracker.domain.model.Category

class GetTransactionCountByCategoryUseCase constructor(
    private val repository: TransactionRepository
){

    suspend operator fun invoke(category: Category): Long {
        return repository.getTransactionCountByCategory(category.id)
    }

}