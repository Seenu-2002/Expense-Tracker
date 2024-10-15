package com.ajay.seenu.expensetracker.android.domain.usecases.transaction

import com.ajay.seenu.expensetracker.android.data.TransactionRepository
import com.ajay.seenu.expensetracker.android.domain.data.Transaction
import com.ajay.seenu.expensetracker.android.domain.mapper.CategoryMapper.map
import javax.inject.Inject

class AddTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository,
) {

    suspend fun addTransaction(transaction: Transaction): Long {
        return repository.addTransaction(
            transaction.type.map(),
            transaction.amount,
            transaction.category.map(),
            transaction.paymentType,
            transaction.date.time,
            transaction.note,
            null, // todo
            null // todo
        )
    }

}