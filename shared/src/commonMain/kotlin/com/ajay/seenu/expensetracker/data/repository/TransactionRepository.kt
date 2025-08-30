package com.ajay.seenu.expensetracker.data.repository

import co.touchlab.kermit.Logger
import com.ajay.seenu.expensetracker.GetTotalAmountByCategoryAndTypeBetween
import com.ajay.seenu.expensetracker.GetTotalExpenseByCategoryBetween
import com.ajay.seenu.expensetracker.TransactionDetailEntity
import com.ajay.seenu.expensetracker.data.data_source.TransactionDataSource
import com.ajay.seenu.expensetracker.data.mapper.toDomain
import com.ajay.seenu.expensetracker.data.mapper.toEntity
import com.ajay.seenu.expensetracker.data.model.TransactionTypeEntity
import com.ajay.seenu.expensetracker.domain.model.Account
import com.ajay.seenu.expensetracker.domain.model.Category
import com.ajay.seenu.expensetracker.domain.model.DateRange
import com.ajay.seenu.expensetracker.domain.model.ExpensePerDay
import com.ajay.seenu.expensetracker.domain.model.OverallData
import com.ajay.seenu.expensetracker.domain.model.PaginationData
import com.ajay.seenu.expensetracker.domain.model.Transaction
import com.ajay.seenu.expensetracker.domain.model.TransactionType
import com.ajay.seenu.expensetracker.util.toEpochMillis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class TransactionRepository constructor(
    private val transactionLocalDataSource: TransactionDataSource,
    private val categoryRepository: CategoryRepository,
    private val accountRepository: AccountRepository
) {

    suspend fun getAllTransactions(pageNo: Int, count: Int): PaginationData<List<Transaction>> {
        return withContext(Dispatchers.IO) {
            val paginationData = transactionLocalDataSource.getAllTransactions(pageNo, count)
            val transactions = parseTransactions(paginationData.data)
            PaginationData(transactions, paginationData.hasMoreData)
        }
    }

    suspend fun getAllTransactionsAsFlow(
        pageNo: Int,
        count: Int
    ): Flow<PaginationData<List<Transaction>>> {
        return withContext(Dispatchers.IO) {
            transactionLocalDataSource.getAllTransactionsAsFlow(pageNo, count).map {
                val transactions = parseTransactions(it.data)
                PaginationData(transactions, it.hasMoreData)
            }
        }
    }

    suspend fun getAllTransactionsBetween(
        pageNo: Int,
        count: Int,
        dateRange: DateRange
    ): Flow<PaginationData<List<Transaction>>> {
        return withContext(Dispatchers.IO) {
            transactionLocalDataSource.getAllTransactionsBetweenAsFlow(
                pageNo,
                count,
                dateRange.start.toEpochMillis(),
                dateRange.end.toEpochMillis()
            ).map {
                val transactions = parseTransactions(it.data)
                PaginationData(transactions, it.hasMoreData)
            }
        }
    }

    private suspend fun parseTransactions(transactions: List<TransactionDetailEntity>): List<Transaction> {
        val categories = categoryRepository.getAllCategories().associateBy { it.id }
        val accounts = accountRepository.getAllAccounts().associateBy { it.id }
        return transactions.mapNotNull { transaction ->
            val category = categories[transaction.categoryId] ?: run {
                Logger.e("Category(${transaction.categoryId}) not found for transaction: ${transaction.id}")
                return@mapNotNull null
            }

            val account = accounts[transaction.accountId] ?: run {
                Logger.e("Account(${transaction.accountId}) not found for transaction: ${transaction.id}")
                return@mapNotNull null
            }
            transaction.toDomain(category = category, account = account)
        }
    }

    suspend fun getTransaction(id: Long): Transaction {
        val transactionEntity = transactionLocalDataSource.getTransaction(id)
        val category = categoryRepository.getCategory(transactionEntity.categoryId)
        val account = accountRepository.getAccountById(transactionEntity.accountId)
            ?: throw IllegalArgumentException("Account not found for id: ${transactionEntity.accountId}")
        return transactionEntity.toDomain(category, account)
    }

    suspend fun addTransaction(
        type: TransactionType,
        amount: Double,
        category: Category,
        account: Account,
        createdAt: Long,
        note: String?,
        place: String?
    ): Long {
        return withContext(Dispatchers.IO) {
            transactionLocalDataSource.addTransaction(
                type = type.toEntity(),
                amount = amount,
                category = category.toEntity(),
                account = account.toEntity(),
                createdAt = createdAt,
                note = note,
                place = place
            )
        }
    }

    suspend fun updateTransaction(
        id: Long,
        type: TransactionType,
        amount: Double,
        category: Category,
        account: Account,
        createdAt: Long,
        note: String?,
        place: String?
    ): Long {
        return withContext(Dispatchers.IO) {
            transactionLocalDataSource.updateTransaction(
                id = id,
                type = type.toEntity(),
                amount = amount,
                category = category.toEntity(),
                account = account.toEntity(),
                createdAt = createdAt,
                note = note,
                place = place
            )
        }
    }

    suspend fun deleteAllTransactions() {
        withContext(Dispatchers.IO) {
            transactionLocalDataSource.deleteAllTransactions()
        }
    }

    suspend fun deleteAllTransactionsByType(type: TransactionTypeEntity) {
        withContext(Dispatchers.IO) {
            transactionLocalDataSource.deleteAllTransactionsByType(type)
        }
    }

    suspend fun deleteTransaction(id: Long) {
        withContext(Dispatchers.IO) {
            transactionLocalDataSource.deleteTransaction(id)
        }
    }

    suspend fun getOverallDataBetween(dateRange: DateRange): OverallData {
        return withContext(Dispatchers.IO) {
            val start = dateRange.start.toEpochMillis()
            val end = dateRange.end.toEpochMillis()
            val income = transactionLocalDataSource.getSumOfAmountBetweenByType(
                TransactionTypeEntity.INCOME,
                start,
                end
            )
            val expense = transactionLocalDataSource.getSumOfAmountBetweenByType(
                TransactionTypeEntity.EXPENSE,
                start,
                end
            )
            OverallData(income = income, expense = expense)
        }
    }

    suspend fun getTotalTransactionPerDayByType(
        type: TransactionType,
        dateRange: DateRange
    ): List<ExpensePerDay> {
        return withContext(Dispatchers.IO) {
            val categories = categoryRepository.getCategories(type)
            transactionLocalDataSource.getTotalTransactionPerDayByType(
                type.toEntity(),
                dateRange.start.toEpochMillis(),
                dateRange.end.toEpochMillis()
            ).mapNotNull {
                it.totalAmount?.let { sum ->
                    ExpensePerDay(
                        Instant.fromEpochMilliseconds(it.createdAt),
                        categories.find { category -> category.id == it.categoryId }!!,
                        sum
                    )
                }
            }
        }
    }

    suspend fun getExpensePerDayByCategory(dateRange: DateRange): List<GetTotalExpenseByCategoryBetween> {
        return withContext(Dispatchers.IO) {
            transactionLocalDataSource.getExpenseByCategory(
                dateRange.start.toEpochMillis(),
                dateRange.end.toEpochMillis()
            )
        }
    }

    suspend fun getTotalAmountByCategory(
        type: TransactionType,
        dateRange: DateRange
    ): List<GetTotalAmountByCategoryAndTypeBetween> {
        return withContext(Dispatchers.IO) {
            transactionLocalDataSource.getTotalAmountByCategoryAndType(
                type.toEntity(),
                dateRange.start.toEpochMillis(),
                dateRange.end.toEpochMillis()
            )
        }
    }

    suspend fun replaceCategoryInTransactions(oldCategory: Long, newCategory: Long) {
        withContext(Dispatchers.IO) {
            transactionLocalDataSource.replaceCategory(oldCategory, newCategory)
        }
    }

    suspend fun getTransactionCountByCategory(categoryId: Long): Long {
        return withContext(Dispatchers.IO) {
            transactionLocalDataSource.getTransactionCountByCategory(categoryId)
        }
    }

}