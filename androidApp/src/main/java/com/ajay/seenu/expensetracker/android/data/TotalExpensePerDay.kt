package com.ajay.seenu.expensetracker.android.data

import com.ajay.seenu.expensetracker.android.domain.data.Transaction
import java.util.Date

data class TotalExpensePerDay constructor(
    val date: Date,
    val amount: Double,
    val expensePerCategory: List<ExpenseByCategory>
)

data class ExpenseByCategory constructor(
    val category: Transaction.Category,
    val amount: Double
) {

    companion object {
        fun getEmpty(category: Transaction.Category): ExpenseByCategory {
            return ExpenseByCategory(category, 0.0)
        }
    }

}