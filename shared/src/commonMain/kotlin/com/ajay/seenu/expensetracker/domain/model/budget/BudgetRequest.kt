package com.ajay.seenu.expensetracker.domain.model.budget

import com.ajay.seenu.expensetracker.domain.model.DateFilter
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

data class BudgetRequest @OptIn(ExperimentalTime::class) constructor(
    val name: String,
    val categoryId: Long? = null,
    val amount: Double,
    val periodType: DateFilter,
    val startDate: Long = Clock.System.now().toEpochMilliseconds(),
    val endDate: Long? = null,
    val isRecurring: Boolean = true
)