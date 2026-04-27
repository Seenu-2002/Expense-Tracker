package com.ajay.seenu.expensetracker.data.data_source

import com.ajay.seenu.expensetracker.BudgetEntity
import com.ajay.seenu.expensetracker.GetActiveBudgetsWithSpendingForCategory
import com.ajay.seenu.expensetracker.GetAllActiveBudgetsWithSpending
import kotlinx.coroutines.flow.Flow

interface BudgetDataSource {

    fun selectAllActiveBudgets(): Flow<List<BudgetEntity>>

    fun selectBudgetsByCategory(categoryId: Long?): List<BudgetEntity>

    fun selectOverallBudgets(): Flow<List<BudgetEntity>>

    fun selectOverallBudgetsList(): List<BudgetEntity>

    fun selectBudgetById(id: Long): BudgetEntity?

    fun insertBudget(
        name: String,
        categoryId: Long?,
        amount: Double,
        periodType: String,
        startDate: Long,
        endDate: Long?,
        isRecurring: Long,
        alertEnabled: Long,
        alertThresholdPercentage: Double
    )

    fun getLastInsertRowId(): Long

    fun updateBudget(
        id: Long,
        name: String,
        categoryId: Long?,
        amount: Double,
        periodType: String,
        startDate: Long,
        endDate: Long?,
        isRecurring: Long,
        alertEnabled: Long,
        alertThresholdPercentage: Double
    )

    fun deleteBudget(id: Long)

    fun getBudgetSpendingForPeriod(
        startDate: Long,
        endDate: Long,
        budgetId: Long
    ): Double

    fun getAllActiveBudgetsWithSpending(
        startDate: Long,
        endDate: Long
    ): Flow<List<GetAllActiveBudgetsWithSpending>>

    fun getActiveBudgetsWithSpendingForCategory(
        startDate: Long,
        endDate: Long,
        categoryId: Long?
    ): List<GetActiveBudgetsWithSpendingForCategory>

    fun updateLastAlertTime(epochSeconds: Long, budgetId: Long)
}

