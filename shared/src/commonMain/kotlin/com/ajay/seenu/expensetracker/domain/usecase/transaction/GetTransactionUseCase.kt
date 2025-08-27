package com.ajay.seenu.expensetracker.domain.usecase.transaction

import com.ajay.seenu.expensetracker.data.repository.TransactionRepository
import com.ajay.seenu.expensetracker.domain.model.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext

class GetTransactionUseCase constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(id: Long): Flow<Transaction> {
        return withContext(Dispatchers.IO) {
            flowOf(repository.getTransaction(id))
        }
    }
}