package com.ajay.seenu.expensetracker.domain.model.budget

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class Budget @OptIn(ExperimentalTime::class) constructor(
    val id: Long,
    val name: String,
    val categoryId: Long?,
    val amount: Double,
    val periodType: String,
    val startDate: Instant,
    val endDate: Instant?,
    val isRecurring: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant,
    val isActive: Boolean,
    val alertEnabled: Boolean,
    val alertThresholdPercentage: Double,
    val lastAlertTriggeredAt: Instant?
)