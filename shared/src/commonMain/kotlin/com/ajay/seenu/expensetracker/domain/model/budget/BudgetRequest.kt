package com.ajay.seenu.expensetracker.domain.model.budget

import com.ajay.seenu.expensetracker.domain.model.DateFilter
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class BudgetRequest @OptIn(ExperimentalTime::class) constructor(
    val name: String,
    val categoryId: Long? = null,
    val amount: Double,
    val periodType: DateFilter,
    val startDate: Long = Clock.System.now().epochSeconds,
    val endDate: Long? = null,
    val isRecurring: Boolean = true,
    val alertEnabled: Boolean,
    val alertThresholdPercentage: Double,
)