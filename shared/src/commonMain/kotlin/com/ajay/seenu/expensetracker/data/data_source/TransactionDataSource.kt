package com.ajay.seenu.expensetracker.data.data_source

import com.ajay.seenu.expensetracker.AccountEntity
import com.ajay.seenu.expensetracker.CategoryEntity
import com.ajay.seenu.expensetracker.GetTotalAmountByCategoryAndTypeBetween
import com.ajay.seenu.expensetracker.GetTotalExpenseByCategoryBetween
import com.ajay.seenu.expensetracker.GetTotalTransactionPerDayByTypeBetween
import com.ajay.seenu.expensetracker.TransactionDetailEntity
import com.ajay.seenu.expensetracker.data.model.TransactionTypeEntity
import com.ajay.seenu.expensetracker.domain.model.PaginationData
import kotlinx.coroutines.flow.Flow

interface TransactionDataSource {
    fun getAllTransactions(pageNo: Int, count: Int): PaginationData<List<TransactionDetailEntity>>
    fun getAllTransactionsAsFlow(
        pageNo: Int,
        count: Int
    ): Flow<PaginationData<List<TransactionDetailEntity>>>
    fun getAllTransactionsBetween(
        pageNo: Int,
        count: Int,
        fromValue: Long,
        toValue: Long
    ): PaginationData<List<TransactionDetailEntity>>
    fun getAllTransactionsBetweenAsFlow(
        pageNo: Int,
        count: Int,
        fromValue: Long,
        toValue: Long
    ): Flow<PaginationData<List<TransactionDetailEntity>>>

    fun getAllTransactionsByType(
        type: TransactionTypeEntity,
        pageNo: Int,
        count: Int
    ): List<TransactionDetailEntity>

    fun getTransaction(id: Long): TransactionDetailEntity
    fun addTransaction(
        type: TransactionTypeEntity,
        amount: Double,
        category: CategoryEntity,
        account: AccountEntity,
        createdAt: Long,
        note: String?,
        place: String?
    ): Long

    fun updateTransaction(
        id: Long,
        type: TransactionTypeEntity,
        amount: Double,
        category: CategoryEntity,
        account: AccountEntity,
        createdAt: Long,
        note: String?,
        place: String?
    ): Long

    fun deleteAllTransactions()
    fun deleteAllTransactionsByType(type: TransactionTypeEntity)
    fun deleteTransaction(id: Long)
    fun getSumOfAmountByType(type: TransactionTypeEntity): Double
    fun getSumOfAmountBetweenByType(
        type: TransactionTypeEntity,
        fromValue: Long,
        toValue: Long
    ): Double

    fun getAllCategories(): List<CategoryEntity>
    fun getCategories(type: TransactionTypeEntity): List<CategoryEntity>
    fun getTotalTransactionPerDayByType(
        type: TransactionTypeEntity,
        startDate: Long,
        endDate: Long
    ): List<GetTotalTransactionPerDayByTypeBetween>

    fun getExpenseByCategory(startDate: Long, endDate: Long): List<GetTotalExpenseByCategoryBetween>
    fun getTotalAmountByCategoryAndType(
        type: TransactionTypeEntity,
        startDate: Long,
        endDate: Long
    ): List<GetTotalAmountByCategoryAndTypeBetween>

    fun replaceCategory(oldCategory: Long, newCategory: Long)
    fun getTransactionCountByCategory(categoryId: Long): Long
}