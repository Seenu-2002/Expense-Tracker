package com.ajay.seenu.expensetracker.android.data

import com.ajay.seenu.expensetracker.TransactionDataSource
import com.ajay.seenu.expensetracker.TransactionDetail
import com.ajay.seenu.expensetracker.android.presentation.widgets.OverallData
import com.ajay.seenu.expensetracker.entity.Category
import com.ajay.seenu.expensetracker.entity.PaymentType
import com.ajay.seenu.expensetracker.entity.TransactionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TransactionRepository @Inject constructor(private val dataSource: TransactionDataSource) {

    fun getAllTransactions(): List<TransactionDetail> {
        return dataSource.getAllTransactions()
    }

    fun getAllTransactionsByType(type: TransactionType): Flow<List<TransactionDetail>> {
        return listOf(dataSource.getAllTransactionsByType(type)).asFlow()
    }

    fun getTransaction(id: Long): TransactionDetail {
        return dataSource.getTransaction(id)
    }

    suspend fun addTransaction(
        type: TransactionType,
        amount: Double,
        category: Category,
        paymentType: PaymentType,
        date: Long,
        note: String?,
        payer: String?,
        place: String?
    ) {
        withContext(Dispatchers.IO) {
            dataSource.addTransaction(
                type = type,
                amount = amount,
                category = category,
                paymentType = paymentType,
                date = date,
                note = note,
                payer = payer,
                place = place
            )
        }
    }

    suspend fun deleteAllTransactions() {
        withContext(Dispatchers.IO) {
            dataSource.deleteAllTransactions()
        }
    }

    suspend fun deleteAllTransactionsByType(type: TransactionType) {
        withContext(Dispatchers.IO) {
            dataSource.deleteAllTransactionsByType(type)
        }
    }

    suspend fun deleteTransaction(id: Long) {
        withContext(Dispatchers.IO) {
            dataSource.deleteTransaction(id)
        }
    }

    fun getOverallData(): OverallData {
        val income = dataSource.getSumOfAmountByType(TransactionType.INCOME)
        val expense = dataSource.getSumOfAmountByType(TransactionType.EXPENSE)
        return OverallData(income = income, expense = expense)
    }
}