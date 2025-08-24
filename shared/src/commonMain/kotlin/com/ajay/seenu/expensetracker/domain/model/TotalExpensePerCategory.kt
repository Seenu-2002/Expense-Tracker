package com.ajay.seenu.expensetracker.domain.model


data class TotalExpensePerCategory constructor(
    val category: Category,
    val amount: Double
)
