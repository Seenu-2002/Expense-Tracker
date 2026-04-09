package com.ajay.seenu.expensetracker.domain.usecase.transaction

import com.ajay.seenu.expensetracker.data.repository.TransactionRepository
import com.ajay.seenu.expensetracker.domain.model.PaginationData
import com.ajay.seenu.expensetracker.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

class GetTrashTransactionsUseCase constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(
        pageNo: Int = 1,
        count: Int = 50
    ): Flow<PaginationData<List<Transaction>>> {
        return repository.getDeletedTransactions(pageNo, count)
    }
}
