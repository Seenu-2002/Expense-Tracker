package com.ajay.seenu.expensetracker.data.mapper

import com.ajay.seenu.expensetracker.BudgetEntity
import com.ajay.seenu.expensetracker.GetActiveBudgetsWithSpendingForCategory
import com.ajay.seenu.expensetracker.GetAllActiveBudgetsWithSpending
import com.ajay.seenu.expensetracker.domain.model.budget.Budget
import com.ajay.seenu.expensetracker.domain.model.budget.BudgetWithSpending
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun BudgetEntity.toDomain(): Budget {
    return Budget(
        id = id,
        name = name,
        categoryId = categoryId,
        amount = amount,
        periodType = periodType,
        startDate = Instant.fromEpochSeconds(startDate),
        endDate = endDate?.let {  Instant.fromEpochSeconds(endDate) },
        isRecurring = isRecurring == 1L,
        createdAt = Instant.fromEpochSeconds(createdAt),
        updatedAt = Instant.fromEpochSeconds(updatedAt),
        isActive = isActive == 1L,
        alertEnabled = alertEnabled == 1L,
        alertThresholdPercentage = alertThresholdPercentage,
        lastAlertTriggeredAt = lastAlertTriggeredAt?.let {  Instant.fromEpochSeconds(lastAlertTriggeredAt) }
    )
}

@OptIn(ExperimentalTime::class)
fun GetAllActiveBudgetsWithSpending.toDomain(): BudgetWithSpending {
    val budget = Budget(
        id = id,
        name = name,
        categoryId = categoryId,
        amount = amount,
        periodType = periodType,
        startDate = Instant.fromEpochSeconds(startDate),
        endDate = endDate?.let { Instant.fromEpochSeconds(it) },
        isRecurring = isRecurring == 1L,
        createdAt = Instant.fromEpochSeconds(createdAt),
        updatedAt = Instant.fromEpochSeconds(updatedAt),
        isActive = isActive == 1L,
        alertEnabled = alertEnabled == 1L,
        alertThresholdPercentage = alertThresholdPercentage,
        lastAlertTriggeredAt = lastAlertTriggeredAt?.let { Instant.fromEpochSeconds(it) }
    )
    return BudgetWithSpending(budget = budget, spentAmount = spentAmount)
}

@OptIn(ExperimentalTime::class)
fun GetActiveBudgetsWithSpendingForCategory.toDomain(): BudgetWithSpending {
    val budget = Budget(
        id = id,
        name = name,
        categoryId = categoryId,
        amount = amount,
        periodType = periodType,
        startDate = Instant.fromEpochSeconds(startDate),
        endDate = endDate?.let { Instant.fromEpochSeconds(it) },
        isRecurring = isRecurring == 1L,
        createdAt = Instant.fromEpochSeconds(createdAt),
        updatedAt = Instant.fromEpochSeconds(updatedAt),
        isActive = isActive == 1L,
        alertEnabled = alertEnabled == 1L,
        alertThresholdPercentage = alertThresholdPercentage,
        lastAlertTriggeredAt = lastAlertTriggeredAt?.let { Instant.fromEpochSeconds(it) }
    )
    return BudgetWithSpending(budget = budget, spentAmount = spentAmount)
}

@OptIn(ExperimentalTime::class)
fun Budget.toEntity(): BudgetEntity {
    return BudgetEntity(
        id = id,
        name = name,
        categoryId = categoryId,
        amount = amount,
        periodType = periodType,
        startDate = startDate.epochSeconds,
        endDate = endDate?.epochSeconds,
        isRecurring = if (isRecurring) 1L else 0L,
        createdAt = createdAt.epochSeconds,
        updatedAt = updatedAt.epochSeconds,
        isActive = if (isActive) 1L else 0L,
        alertEnabled = if(alertEnabled) 1L else 0L,
        alertThresholdPercentage = alertThresholdPercentage,
        lastAlertTriggeredAt = lastAlertTriggeredAt?.epochSeconds,
    )
}