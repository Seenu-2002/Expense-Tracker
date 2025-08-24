package com.ajay.seenu.expensetracker.domain.usecase.transaction

import com.ajay.seenu.expensetracker.data.repository.TransactionRepository

class DeleteAllTransactionsUseCase constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke() {
        repository.deleteAllTransactions()
    }
}