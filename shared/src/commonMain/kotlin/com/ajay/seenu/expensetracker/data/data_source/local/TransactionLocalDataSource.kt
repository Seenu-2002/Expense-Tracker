package com.ajay.seenu.expensetracker.data.data_source.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.ajay.seenu.expensetracker.AccountEntity
import com.ajay.seenu.expensetracker.CategoryEntity
import com.ajay.seenu.expensetracker.ExpenseDatabase
import com.ajay.seenu.expensetracker.GetTotalAmountByCategoryAndTypeBetween
import com.ajay.seenu.expensetracker.GetTotalExpenseByCategoryBetween
import com.ajay.seenu.expensetracker.GetTotalTransactionPerDayByTypeBetween
import com.ajay.seenu.expensetracker.TransactionDetailEntity
import com.ajay.seenu.expensetracker.data.data_source.TransactionDataSource
import com.ajay.seenu.expensetracker.data.model.TransactionTypeEntity
import com.ajay.seenu.expensetracker.domain.model.PaginationData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionLocalDataSource constructor(
    private val database: ExpenseDatabase
) : TransactionDataSource {
    private val queries = database.expenseDatabaseQueries

    override fun getAllTransactions(
        pageNo: Int,
        count: Int
    ): PaginationData<List<TransactionDetailEntity>> {
        require(pageNo > 0) { "Page number should be greater than 0" }
        require(count > 0) { "Count should be greater than 0" }

        return queries.getAllTransactions((count + 1).toLong(), (pageNo - 1L) * count)
            .executeAsList().let {
                val hasMoreData = it.size > count
                val data = if (hasMoreData) {
                    it.subList(0, count)
                } else {
                    it
                }
                PaginationData(data, hasMoreData)
            }
    }

    override fun getAllTransactionsAsFlow(
        pageNo: Int,
        count: Int
    ): Flow<PaginationData<List<TransactionDetailEntity>>> {
        require(pageNo > 0) { "Page number should be greater than 0" }
        require(count > 0) { "Count should be greater than 0" }

        return queries.getAllTransactions((count + 1).toLong(), (pageNo - 1L) * count)
            .asFlow().mapToList(Dispatchers.IO).map {
                val hasMoreData = it.size > count
                val data = if (hasMoreData) {
                    it.subList(0, count)
                } else {
                    it
                }
                PaginationData(data, hasMoreData)
            }
    }

    override fun getAllTransactionsBetween(
        pageNo: Int,
        count: Int,
        fromValue: Long,
        toValue: Long
    ): PaginationData<List<TransactionDetailEntity>> {
        if (pageNo == 0) {
            return PaginationData(emptyList(), false)
        }
        return queries.getAllTransactionsBetween(
            startUTCValue = fromValue,
            endUTCValue = toValue,
            limit = (count + 1).toLong(),
            offset = (pageNo - 1L) * count
        ).executeAsList().let {
            val hasMoreData = it.size > count
            val data = if (hasMoreData) {
                it.subList(0, count)
            } else {
                it
            }
            PaginationData(data, hasMoreData)
        }
    }

    override fun getAllTransactionsBetweenAsFlow(
        pageNo: Int,
        count: Int,
        fromValue: Long,
        toValue: Long
    ): Flow<PaginationData<List<TransactionDetailEntity>>> {
        require(pageNo > 0) { "Page number should be greater than 0" }
        require(count > 0) { "Count should be greater than 0" }

        return queries.getAllTransactionsBetween(
            startUTCValue = fromValue,
            endUTCValue = toValue,
            limit = (count + 1).toLong(),
            offset = (pageNo - 1L) * count
        ).asFlow().mapToList(Dispatchers.IO).map {
            val hasMoreData = it.size > count
            val data = if (hasMoreData) {
                it.subList(0, count)
            } else {
                it
            }
            PaginationData(data, hasMoreData)
        }
    }

    override fun getAllTransactionsByType(
        type: TransactionTypeEntity,
        pageNo: Int,
        count: Int
    ): List<TransactionDetailEntity> {
        if (pageNo == 0) {
            return emptyList()
        }
        return queries.getAllTransactionsByType(type, count.toLong(), (pageNo - 1L) * count)
            .executeAsList()
    }

    override fun getTransaction(id: Long): TransactionDetailEntity {
        return queries.getTransaction(id).executeAsOne()
    }

    override fun addTransaction(
        type: TransactionTypeEntity,
        amount: Double,
        category: CategoryEntity,
        account: AccountEntity,
        createdAt: Long,
        note: String?,
        place: String?
    ): Long {
        queries.addTransaction(
            type = type,
            amount = amount,
            categoryId = category.id,
            accountId = account.id,
            note = note,
            createdAt = createdAt,
            place = place
        )
        return queries.getLastInsertTransactionRowId().executeAsOne()
    }

    override fun updateTransaction(
        id: Long,
        type: TransactionTypeEntity,
        amount: Double,
        category: CategoryEntity,
        account: AccountEntity,
        createdAt: Long,
        note: String?,
        place: String?
    ): Long {
        queries.updateTransaction(
            id = id,
            type = type,
            amount = amount,
            categoryId = category.id,
            accountId = account.id,
            note = note,
            createdAt = createdAt,
            place = place
        )
        return queries.getLastInsertTransactionRowId().executeAsOne()
    }

    override fun deleteAllTransactions() {
        queries.deleteAllTransactions()
    }

    override fun deleteAllTransactionsByType(type: TransactionTypeEntity) {
        queries.deleteAllTransactionsByType(type)
    }

    override fun deleteTransaction(id: Long) {
        queries.deleteTransaction(id)
    }

    override fun getSumOfAmountByType(type: TransactionTypeEntity): Double {
        return queries.getSumOfAmountByType(type).executeAsOne().sum ?: 0.0
    }

    override fun getSumOfAmountBetweenByType(
        type: TransactionTypeEntity,
        fromValue: Long,
        toValue: Long
    ): Double {
        return queries.getSumOfAmountBetweenByType(
            type = type,
            startUTCValue = fromValue,
            endUTCValue = toValue
        ).executeAsOne().sum ?: 0.0
    }

    override fun getAllCategories(): List<CategoryEntity> {
        return queries.getAllCategories().executeAsList()
    }

    override fun getCategories(type: TransactionTypeEntity): List<CategoryEntity> {
        return queries.getCategories(type).executeAsList()
    }

    override fun getTotalTransactionPerDayByType(
        type: TransactionTypeEntity,
        startDate: Long,
        endDate: Long
    ): List<GetTotalTransactionPerDayByTypeBetween> {
        return queries.getTotalTransactionPerDayByTypeBetween(type, startDate, endDate)
            .executeAsList()
    }

    override fun getExpenseByCategory(
        startDate: Long,
        endDate: Long
    ): List<GetTotalExpenseByCategoryBetween> {
        return queries.getTotalExpenseByCategoryBetween(startDate, endDate).executeAsList()
    }

    override fun getTotalAmountByCategoryAndType(
        type: TransactionTypeEntity,
        startDate: Long,
        endDate: Long
    ): List<GetTotalAmountByCategoryAndTypeBetween> {
        return queries.getTotalAmountByCategoryAndTypeBetween(type, startDate, endDate)
            .executeAsList()
    }

    override fun replaceCategory(oldCategory: Long, newCategory: Long) {
        return queries.changeCategory(oldCategory = oldCategory, newCategory = newCategory)
    }

    override fun getTransactionCountByCategory(categoryId: Long): Long {
        return queries.numberOfTransactionsByCategory(categoryId).executeAsOne()
    }

}