package com.ajay.seenu.expensetracker.entity.budget

data class BudgetSummary(
    val totalBudgets: Int,
    val totalBudgetAmount: Double,
    val totalSpent: Double,
    val overBudgetCount: Int,
    val budgetsWithSpending: List<BudgetWithSpending>
)