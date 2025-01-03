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
                       place: String?): Long
    fun deleteAllTransactions()
    fun deleteAllTransactionsByType(type: TransactionType)
    fun deleteTransaction(id: Long)
    fun getSumOfAmountByType(type: TransactionType): Double
    fun getSumOfAmountBetweenByType(type: TransactionType, fromValue: Long, toValue: Long): Double
    fun getAllCategories(): List<Category>
    fun getCategories(type: TransactionType): List<Category>
    fun getTotalTransactionPerDayByType(type: TransactionType, startDate: Long, endDate: Long): List<GetTotalTransactionPerDayByTypeBetween>
    fun getExpenseByPaymentType(startDate: Long, endDate: Long): List<GetTotalExpenseByPaymentTypeBetween>
    fun getExpenseByCategory(startDate: Long, endDate: Long): List<GetTotalExpenseByCategoryBetween>
    fun getTotalAmountByCategoryAndType(type: TransactionType, startDate: Long, endDate: Long): List<GetTotalAmountByCategoryAndTypeBetween>
}