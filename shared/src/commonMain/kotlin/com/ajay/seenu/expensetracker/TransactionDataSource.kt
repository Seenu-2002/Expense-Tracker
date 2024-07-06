package com.ajay.seenu.expensetracker

import com.ajay.seenu.expensetracker.entity.PaymentType
import com.ajay.seenu.expensetracker.entity.TransactionType

interface TransactionDataSource {
    fun getAllTransactions(pageNo: Int, count: Int): PaginationData<List<TransactionDetail>>
    fun getAllTransactionsBetween(pageNo: Int, count: Int, fromValue: Long, toValue: Long): PaginationData<List<TransactionDetail>>
    fun getAllTransactionsByType(type: TransactionType,pageNo: Int, count: Int): List<TransactionDetail>
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
    fun getAllCategories(): List<Category>
    fun getCategories(type: TransactionType): List<Category>
    fun getTotalTransactionPerDayByType(type: TransactionType): List<GetTotalTransactionPerDayByType>
    fun getExpenseByPaymentType(): List<GetTotalExpenseByPaymentType>
    fun getExpenseByCategory(): List<GetTotalExpenseByCategory>
}