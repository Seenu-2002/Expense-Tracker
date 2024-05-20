package com.ajay.seenu.expensetracker.android.data

import com.ajay.seenu.expensetracker.TransactionDataSource
import com.ajay.seenu.expensetracker.TransactionDetail
import com.ajay.seenu.expensetracker.android.presentation.widgets.OverallData
import com.ajay.seenu.expensetracker.Category
import com.ajay.seenu.expensetracker.PaginationData
import com.ajay.seenu.expensetracker.android.domain.data.Transaction
import com.ajay.seenu.expensetracker.android.domain.mapper.CategoryMapper
import com.ajay.seenu.expensetracker.android.domain.mapper.TransactionMapper.map
import com.ajay.seenu.expensetracker.entity.PaymentType
import com.ajay.seenu.expensetracker.entity.TransactionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TransactionRepository @Inject constructor(private val dataSource: TransactionDataSource) {

    suspend fun getAllTransactions(pageNo: Int, count: Int): PaginationData<List<Transaction>> {
        return withContext(Dispatchers.IO) {
            val paginationData = dataSource.getAllTransactions(pageNo, count)
            val transactions = paginationData.data
            val categories = dataSource.getAllCategories().let {
                CategoryMapper.mapCategories(it)
            }
            transactions.mapNotNull { transaction ->
                val category = categories.find { it.id == transaction.category }
                    ?: return@mapNotNull null
                transaction.map(category)
            }.let {
                PaginationData(it, paginationData.hasMoreData)
            }
        }
    }

    suspend fun getAllTransactionsByType(type: TransactionType, pageNo: Int, count: Int): Flow<List<TransactionDetail>> {
        return listOf(dataSource.getAllTransactionsByType(type, pageNo, count)).asFlow()
    }

    suspend fun getTransaction(id: Long): Transaction {
        val transactionDetail = dataSource.getTransaction(id)
        val categories = dataSource.getAllCategories().let {
            CategoryMapper.mapCategories(it)
        }
        val category = categories.find { it.id == transactionDetail.category }!!
        return transactionDetail.map(category)
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

    suspend fun getOverallData(): OverallData {
        return withContext(Dispatchers.IO) {
            val income = dataSource.getSumOfAmountByType(TransactionType.INCOME)
            val expense = dataSource.getSumOfAmountByType(TransactionType.EXPENSE)
            OverallData(income = income, expense = expense)
        }
    }

    suspend fun getCategories(type: Transaction.Type): List<Category> {
        return withContext(Dispatchers.IO) {
            val transactionType = if (type == Transaction.Type.INCOME) {
                TransactionType.INCOME
            } else {
                TransactionType.EXPENSE
            }
            dataSource.getCategories(transactionType)
        }
    }

}