package com.ajay.seenu.expensetracker.data.mapper

import com.ajay.seenu.expensetracker.BudgetEntity
import com.ajay.seenu.expensetracker.domain.model.budget.Budget
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
        isRecurring = isRecurring,
        createdAt = Instant.fromEpochSeconds(createdAt),
        updatedAt = Instant.fromEpochSeconds(updatedAt),
        isActive = isActive
    )
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
        isRecurring = isRecurring,
        createdAt = createdAt.epochSeconds,
        updatedAt = updatedAt.epochSeconds,
        isActive = isActive
    )
}