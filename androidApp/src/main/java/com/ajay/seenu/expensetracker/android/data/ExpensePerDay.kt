package com.ajay.seenu.expensetracker.android.data

import com.ajay.seenu.expensetracker.android.domain.data.Transaction
import java.util.Date

data class ExpensePerDay constructor(
    val date: Date,
    val category: Transaction.Category,
    val amount: Double
)