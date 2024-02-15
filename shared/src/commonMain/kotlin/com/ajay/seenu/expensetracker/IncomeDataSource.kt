package com.ajay.seenu.expensetracker

import com.ajay.seenu.expensetracker.entity.Category
import com.ajay.seenu.expensetracker.entity.PaymentType

interface IncomeDataSource {
    fun getAllIncomes(): List<Income>
    fun addIncome(income: Income)
}