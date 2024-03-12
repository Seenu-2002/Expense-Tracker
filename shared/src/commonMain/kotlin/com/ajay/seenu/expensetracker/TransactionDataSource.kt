package com.ajay.seenu.expensetracker

import com.ajay.seenu.expensetracker.entity.Category
import com.ajay.seenu.expensetracker.entity.PaymentType
import com.ajay.seenu.expensetracker.entity.TransactionType

interface TransactionDataSource {
    fun getAllTransactions(): List<TransactionDetail>
    fun getAllTransactionsByType(type: TransactionType): List<TransactionDetail>
    fun getTransaction(id: Long): TransactionDetail
    fun addTransaction(type: TransactionType,
                       amount: Long,
                       category: Category,
                       paymentType: PaymentType,
                       note: String?,
                       date: String?,
                       payer: String?,
                       place: String?)
    fun deleteAllTransactions()
    fun deleteAllTransactionsByType(type: TransactionType)
    fun deleteTransaction(id: Long)
}