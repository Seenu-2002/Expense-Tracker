package com.ajay.seenu.expensetracker.android.domain.usecases.transaction

import com.ajay.seenu.expensetracker.android.data.TransactionRepository
import javax.inject.Inject

class DeleteAllTransactionsUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke() {
        repository.deleteAllTransactions()
    }
}