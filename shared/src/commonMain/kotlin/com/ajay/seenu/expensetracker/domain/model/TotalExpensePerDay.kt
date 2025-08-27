package com.ajay.seenu.expensetracker.domain.model

import kotlinx.datetime.LocalDate

data class TotalExpensePerDay constructor(
    val date: LocalDate,
    val amount: Double,
    val expensePerCategory: List<ExpenseByCategory>
)

data class ExpenseByCategory constructor(
    val category: Category,
    val amount: Double
) {

    companion object {
        fun getEmpty(category: Category): ExpenseByCategory {
            return ExpenseByCategory(category, 0.0)
        }
    }

}