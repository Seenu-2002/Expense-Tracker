package com.ajay.seenu.expensetracker.android.data

import com.ajay.seenu.expensetracker.android.domain.data.Transaction

data class TotalExpensePerCategory(
    val category: Transaction.Category,
    val amount: Double
)
