package com.ajay.seenu.expensetracker.data.data_source

import com.ajay.seenu.expensetracker.AccountEntity
import com.ajay.seenu.expensetracker.CategoryEntity
import com.ajay.seenu.expensetracker.GetAllTransactionsBetweenWithDetails
import com.ajay.seenu.expensetracker.GetAllTransactionsWithDetails
import com.ajay.seenu.expensetracker.SearchTransactionsBetweenWithDetails
import com.ajay.seenu.expensetracker.GetDeletedTransactionsWithDetails
import com.ajay.seenu.expensetracker.GetIncomeAndExpensePerDayBetween
import com.ajay.seenu.expensetracker.GetOverallDataBetween
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

    fun getAllTransactionsWithDetails(
        pageNo: Int,
        count: Int
    ): PaginationData<List<GetAllTransactionsWithDetails>>

    fun getAllTransactionsWithDetailsAsFlow(
        pageNo: Int,
        count: Int
    ): Flow<PaginationData<List<GetAllTransactionsWithDetails>>>

    fun getAllTransactionsBetweenWithDetailsAsFlow(
        pageNo: Int,
        count: Int,
        fromValue: Long,
        toValue: Long
    ): Flow<PaginationData<List<GetAllTransactionsBetweenWithDetails>>>

    fun searchTransactionsBetweenWithDetailsAsFlow(
        pageNo: Int,
        count: Int,
        fromValue: Long,
        toValue: Long,
        query: String
    ): Flow<PaginationData<List<SearchTransactionsBetweenWithDetails>>>
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
        toAccountId: Long?,
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
        toAccountId: Long?,
        createdAt: Long,
        note: String?,
        place: String?
    ): Long

    fun softDeleteTransaction(id: Long, deletedAt: Long)
    fun restoreTransaction(id: Long)
    fun getDeletedTransactionsWithDetails(pageNo: Int, count: Int): Flow<PaginationData<List<GetDeletedTransactionsWithDetails>>>
    fun getDeletedTransactionsCount(): Long
    fun permanentlyDeleteTransaction(id: Long)
    fun purgeOldDeletedTransactions(cutoffMs: Long)
    fun deleteAllTransactions()
    fun deleteAllTransactionsByType(type: TransactionTypeEntity)
    fun deleteTransaction(id: Long)
    fun getSumOfAmountByType(type: TransactionTypeEntity): Double
    fun getSumOfAmountBetweenByType(
        type: TransactionTypeEntity,
        fromValue: Long,
        toValue: Long
    ): Double
    fun getOverallDataBetweenAsFlow(startDate: Long, endDate: Long): Flow<GetOverallDataBetween>

    fun getAllCategories(): List<CategoryEntity>
    fun getCategories(type: TransactionTypeEntity): List<CategoryEntity>
    fun getTotalTransactionPerDayByType(
        type: TransactionTypeEntity,
        startDate: Long,
        endDate: Long
    ): List<GetTotalTransactionPerDayByTypeBetween>

    fun getExpenseByCategory(startDate: Long, endDate: Long): List<GetTotalExpenseByCategoryBetween>
    fun getIncomeAndExpensePerDay(
        startDate: Long,
        endDate: Long
    ): List<GetIncomeAndExpensePerDayBetween>
    fun getTotalAmountByCategoryAndType(
        type: TransactionTypeEntity,
        startDate: Long,
        endDate: Long
    ): List<GetTotalAmountByCategoryAndTypeBetween>

    fun getTotalExpenseByCategoryInPeriod(
        categoryId: Long?,
        startDate: Long,
        endDate: Long
    ): Double

    fun replaceCategory(oldCategory: Long, newCategory: Long)
    fun getTransactionCountByCategory(categoryId: Long): Long
    fun getTransactionCountByAccount(id: Long): Long

    fun replaceAccount(oldAccount: Long, newAccount: Long)
}