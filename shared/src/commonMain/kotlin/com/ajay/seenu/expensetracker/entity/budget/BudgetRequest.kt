package com.ajay.seenu.expensetracker.entity.budget

data class BudgetRequest(
    val name: String,
    val categoryId: Long? = null,
    val amount: Double,
    val periodType: BudgetPeriodType,
    val startDate: Long = kotlinx.datetime.Clock.System.now().toEpochMilliseconds(),
    val endDate: Long? = null,
    val isRecurring: Boolean = true
)