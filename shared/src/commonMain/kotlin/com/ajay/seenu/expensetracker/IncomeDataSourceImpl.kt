package com.ajay.seenu.expensetracker

import com.ajay.seenu.expensetracker.entity.Category
import com.ajay.seenu.expensetracker.entity.PaymentType

class IncomeDataSourceImpl(
    private val database: ExpenseDatabase
): IncomeDataSource {
    private val queries = database.expenseDatabaseQueries

    override fun getAllIncomes(): List<Income> {
        return queries.getAllIncome().executeAsList()
    }

    override fun addIncome(income: Income) {
        queries.addIncome(income.amount, income.category, income.paymentType)
    }
}