package com.ajay.seenu.expensetracker

import com.ajay.seenu.expensetracker.entity.Category
import com.ajay.seenu.expensetracker.entity.PaymentType
import com.ajay.seenu.expensetracker.entity.TransactionType

interface TransactionDataSource {
    fun getAllTransactions(): List<TransactionDetail>
    fun getAllTransactionsByType(type: TransactionType): List<TransactionDetail>
    fun getTransaction(id: Long): TransactionDetail
    fun addTransaction(type: TransactionType,
                       amount: Double,
                       category: Category,
                       paymentType: PaymentType,
                       date: Long,
                       note: String?,
                       payer: String?,
                       place: String?)
    fun deleteAllTransactions()
    fun deleteAllTransactionsByType(type: TransactionType)
    fun deleteTransaction(id: Long)
    fun getSumOfAmountByType(type: TransactionType): Double
}