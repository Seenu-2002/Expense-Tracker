package com.ajay.seenu.expensetracker

import com.ajay.seenu.expensetracker.entity.Category
import com.ajay.seenu.expensetracker.entity.PaymentType
import com.ajay.seenu.expensetracker.entity.TransactionType


class TransactionDataSourceImpl constructor(
    private val database: ExpenseDatabase
): TransactionDataSource {
    private val queries = database.expenseDatabaseQueries

    override fun getAllTransactions(): List<TransactionDetail> {
        return queries.getAllTransactions().executeAsList()
    }

    override fun getAllTransactionsByType(type: TransactionType): List<TransactionDetail> {
        return queries.getAllTransactionsByType(type).executeAsList()
    }

    override fun getTransaction(id: Long): TransactionDetail {
        return queries.getTransaction(id).executeAsOne()
    }

    override fun addTransaction(type: TransactionType,
                                amount: Long,
                                category: Category,
                                paymentType: PaymentType,
                                note: String?,
                                date: String?,
                                payer: String?,
                                place: String?) {
        queries.addTransaction(
            type = type,
            amount = amount,
            category = category,
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
}