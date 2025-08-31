package com.ajay.seenu.expensetracker.domain.usecase.transaction

import com.ajay.seenu.expensetracker.data.repository.TransactionRepository
import com.ajay.seenu.expensetracker.domain.model.Transaction
import kotlin.time.ExperimentalTime

class AddTransactionUseCase constructor(
    private val repository: TransactionRepository,
) {

    @OptIn(ExperimentalTime::class)
    suspend fun addTransaction(transaction: Transaction): Long {
        return repository.addTransaction(
            transaction.type,
            transaction.amount,
            transaction.category,
            transaction.account,
            transaction.createdAt.toEpochMilliseconds(),
            transaction.note,
            null, // todo
        )
    }

}