package com.ajay.seenu.expensetracker.android.domain.usecases

import com.ajay.seenu.expensetracker.android.data.TransactionRepository
import com.ajay.seenu.expensetracker.android.domain.data.Transaction
import javax.inject.Inject

class GetTransactionCountByCategoryUseCase @Inject constructor(
    private val repository: TransactionRepository
){

    suspend operator fun invoke(category: Transaction.Category): Long {
        return repository.getTransactionCountByCategory(category.id)
    }

}