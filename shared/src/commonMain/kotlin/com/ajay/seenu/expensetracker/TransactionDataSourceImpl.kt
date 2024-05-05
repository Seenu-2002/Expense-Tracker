package com.ajay.seenu.expensetracker

import com.ajay.seenu.expensetracker.Category
import com.ajay.seenu.expensetracker.entity.PaymentType
import com.ajay.seenu.expensetracker.entity.TransactionType
import org.koin.core.logger.Logger


class TransactionDataSourceImpl constructor(
    private val database: ExpenseDatabase
): TransactionDataSource {
    private val queries = ExpenseDatabase(driver,
        TransactionDetailAdapter = TransactionDetail.Adapter(
            typeAdapter = EnumColumnAdapter(),
            categoryAdapter = EnumColumnAdapter(),
            paymentTypeAdapter = EnumColumnAdapter()
        )).expenseDatabaseQueries

    override fun getAllTransactions(pageNo: Int, count: Int): PaginationData<List<TransactionDetail>> {
        if (pageNo == 0) {
            return PaginationData(emptyList(), false)
        }
        return queries.getAllTransactions((count + 1).toLong(), (pageNo - 1L) * count).executeAsList().let {
            val hasMoreData = it.size > count
            val data = if (hasMoreData) {
                it.subList(0, count)
            } else {
                it
            }
            PaginationData(data, hasMoreData)
        }
    }

    override fun getAllTransactionsByType(type: TransactionType, pageNo: Int, count: Int): List<TransactionDetail> {
        if (pageNo == 0) {
            return emptyList()
        }
        return queries.getAllTransactionsByType(type, count.toLong(), (pageNo - 1L) * count).executeAsList()
    }

    override fun getTransaction(id: Long): TransactionDetail {
        return queries.getTransaction(id).executeAsOne()
    }

    override fun addTransaction(
        type: TransactionType,
        amount: Double,
        category: Category,
        paymentType: PaymentType,
        date: Long,
        note: String?,
        payer: String?,
        place: String?
    ) {
        queries.addTransaction(
            type = type,
            amount = amount,
            category = category.id,
            paymentType = paymentType,
            note = note,
            date = date,
            payer = payer,
            place = place)
    }

    override fun deleteAllTransactions() {
        queries.deleteAllTransactions()
    }

    override fun deleteAllTransactionsByType(type: TransactionType) {
        queries.deleteAllTransactionsByType(type)
    }

    override fun deleteTransaction(id: Long) {
        queries.deleteTransaction(id)
    }

    override fun getSumOfAmountByType(type: TransactionType): Double {
        return queries.getSumOfAmountByType(type).executeAsOne().SUM ?: 0.0
    }

    override fun getAllCategories(): List<Category> {
        return queries.getAllCategories().executeAsList()
    }

    override fun getCategories(type: TransactionType): List<Category> {
        return queries.getCategories(type).executeAsList()
    }
}