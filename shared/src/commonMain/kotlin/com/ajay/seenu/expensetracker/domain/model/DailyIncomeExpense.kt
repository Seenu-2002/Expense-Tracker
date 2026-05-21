package com.ajay.seenu.expensetracker.domain.model

import kotlinx.datetime.LocalDate

data class DailyIncomeExpense(
    val date: LocalDate,
    val income: Double,
    val expense: Double,
)
