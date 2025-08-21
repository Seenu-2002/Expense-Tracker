package com.ajay.seenu.expensetracker.entity.budget

import com.ajay.seenu.expensetracker.Budget

data class BudgetWithSpending(
    val budget: Budget,
    val spentAmount: Double,
    val remainingAmount: Double = budget.amount - spentAmount,
    val percentageUsed: Double = if (budget.amount > 0) (spentAmount / budget.amount) * 100 else 0.0,
    val isOverBudget: Boolean = spentAmount > budget.amount
)