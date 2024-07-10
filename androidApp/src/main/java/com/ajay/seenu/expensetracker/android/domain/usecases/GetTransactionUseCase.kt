package com.ajay.seenu.expensetracker.android.domain.usecases

import com.ajay.seenu.expensetracker.TransactionDetail
import com.ajay.seenu.expensetracker.android.data.TransactionRepository
import com.ajay.seenu.expensetracker.android.domain.data.Transaction
import com.ajay.seenu.expensetracker.android.domain.mapper.TransactionMapper.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(id: Long): Flow<Transaction> {
        return withContext(Dispatchers.IO) {
            flowOf(repository.getTransaction(id))
        }
    }
}