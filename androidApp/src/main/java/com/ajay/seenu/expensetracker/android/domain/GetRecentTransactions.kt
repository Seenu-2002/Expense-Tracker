package com.ajay.seenu.expensetracker.android.domain

import com.ajay.seenu.expensetracker.TransactionDetail
import com.ajay.seenu.expensetracker.android.data.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import javax.inject.Inject

class GetRecentTransactions @Inject constructor(
    private val repository: TransactionRepository
) {

    fun invoke(): Flow<List<TransactionDetail>> {
        return listOf(repository.getAllTransactions()).asFlow()
    }

}