package com.ajay.seenu.expensetracker.domain.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class ExpensePerDay constructor(
    val date: Instant,
    val category: Category,
    val amount: Double
)