package com.ajay.seenu.expensetracker.data.repository

import co.touchlab.kermit.Logger
import com.ajay.seenu.expensetracker.GetDeletedTransactionsWithDetails
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
import kotlin.time.Clock
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
            val paginationData = transactionLocalDataSource.getAllTransactionsWithDetails(pageNo, count)
            PaginationData(paginationData.data.map { it.toDomain() }, paginationData.hasMoreData)
        }
    }

    suspend fun getAllTransactionsAsFlow(
        pageNo: Int,
        count: Int
    ): Flow<PaginationData<List<Transaction>>> {
        return withContext(Dispatchers.IO) {
            transactionLocalDataSource.getAllTransactionsWithDetailsAsFlow(pageNo, count).map {
                PaginationData(it.data.map { row -> row.toDomain() }, it.hasMoreData)
            }
        }
    }

    suspend fun getAllTransactionsBetween(
        pageNo: Int,
        count: Int,
        dateRange: DateRange
    ): Flow<PaginationData<List<Transaction>>> {
        return withContext(Dispatchers.IO) {
            transactionLocalDataSource.getAllTransactionsBetweenWithDetailsAsFlow(
                pageNo,
                count,
                dateRange.start.toEpochMillis(),
                dateRange.end.toEpochMillis()
            ).map {
                PaginationData(it.data.map { row -> row.toDomain() }, it.hasMoreData)
            }
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

    suspend fun softDeleteTransaction(id: Long) {
        withContext(Dispatchers.IO) {
            val deletedAt = Clock.System.now().toEpochMilliseconds()
            transactionLocalDataSource.softDeleteTransaction(id, deletedAt)
        }
    }

    suspend fun restoreTransaction(id: Long) {
        withContext(Dispatchers.IO) {
            transactionLocalDataSource.restoreTransaction(id)
        }
    }

    suspend fun getDeletedTransactions(
        pageNo: Int,
        count: Int
    ): Flow<PaginationData<List<Transaction>>> {
        return withContext(Dispatchers.IO) {
            transactionLocalDataSource.getDeletedTransactionsWithDetails(pageNo, count).map {
                PaginationData(it.data.map { row -> row.toDomain() }, it.hasMoreData)
            }
        }
    }

    suspend fun permanentlyDeleteTransaction(id: Long) {
        withContext(Dispatchers.IO) {
            transactionLocalDataSource.permanentlyDeleteTransaction(id)
        }
    }

    suspend fun purgeOldDeletedTransactions(cutoffMs: Long) {
        withContext(Dispatchers.IO) {
            transactionLocalDataSource.purgeOldDeletedTransactions(cutoffMs)
        }
    }

    suspend fun deleteTransaction(id: Long) {
        withContext(Dispatchers.IO) {
            transactionLocalDataSource.deleteTransaction(id)
        }
    }

    fun getOverallDataBetween(dateRange: DateRange): Flow<OverallData> {
        return transactionLocalDataSource.getOverallDataBetweenAsFlow(
            dateRange.start.toEpochMillis(),
            dateRange.end.toEpochMillis()
        ).map { OverallData(income = it.income, expense = it.expense) }
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
                    val category = categories.find { category -> category.id == it.categoryId } ?: run {
                        Logger.e("Category(${it.categoryId}) not found for daily aggregate, skipping")
                        return@mapNotNull null
                    }
                    ExpensePerDay(
                        Instant.fromEpochMilliseconds(it.createdAt),
                        category,
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

    suspend fun getTotalExpenseByCategoryInPeriod(
        categoryId: Long?,
        startDate: Long,
        endDate: Long
    ): Double {
        return withContext(Dispatchers.IO) {
            transactionLocalDataSource.getTotalExpenseByCategoryInPeriod(
                categoryId,
                startDate,
                endDate
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

    suspend fun getTransactionCountByAccountId(accountId: Long): Long {
        return withContext(Dispatchers.IO) {
            transactionLocalDataSource.getTransactionCountByAccount(accountId)
        }
    }

    suspend fun replaceAccountInTransactions(oldAccountId: Long, newAccountId: Long) {
        withContext(Dispatchers.IO) {
            transactionLocalDataSource.replaceAccount(oldAccountId, newAccountId)
        }
    }

}