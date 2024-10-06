package com.ajay.seenu.expensetracker.android.domain.usecases.transaction

import com.ajay.seenu.expensetracker.android.data.TransactionRepository
import javax.inject.Inject

class DeleteTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(id: Long) {
        repository.deleteTransaction(id)
    }
}