package com.ajay.seenu.expensetracker.data.repository

import com.ajay.seenu.expensetracker.data.data_source.BudgetDataSource
import com.ajay.seenu.expensetracker.data.mapper.toDomain
import com.ajay.seenu.expensetracker.domain.model.DateFilter
import com.ajay.seenu.expensetracker.domain.model.DateRange
import com.ajay.seenu.expensetracker.domain.model.budget.Budget
import com.ajay.seenu.expensetracker.domain.model.budget.BudgetRequest
import com.ajay.seenu.expensetracker.domain.model.budget.BudgetSummary
import com.ajay.seenu.expensetracker.domain.model.budget.BudgetWithSpending
import com.ajay.seenu.expensetracker.util.toEpochMillis
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class BudgetRepository(
    private val dataSource: BudgetDataSource
) {

    // Get all active budgets
    fun getAllActiveBudgets(): Flow<List<Budget>> {
        return dataSource.selectAllActiveBudgets()
            .map { budgets ->
                budgets.map { it.toDomain() }
            }
    }

    // Get budgets by category
    fun getBudgetsByCategory(categoryId: Long?): List<Budget> {
        return dataSource.selectBudgetsByCategory(categoryId)
            .map { it.toDomain() }
    }

    // Get overall budgets (not tied to specific categories)
    fun getOverallBudgets(): Flow<List<Budget>> {
        return dataSource.selectOverallBudgets()
            .map { budgets ->
                budgets.map { it.toDomain() }
            }
    }

    // Get overall budgets as a one-shot list
    fun getOverallBudgetsList(): List<Budget> {
        return dataSource.selectOverallBudgetsList()
            .map { it.toDomain() }
    }

    // Get budget by ID
    suspend fun getBudgetById(id: Long): Budget? {
        return dataSource.selectBudgetById(id)?.toDomain()
    }

    // Create new budget
    suspend fun createBudget(budgetRequest: BudgetRequest): Long {
        dataSource.insertBudget(
            name = budgetRequest.name,
            categoryId = budgetRequest.categoryId,
            amount = budgetRequest.amount,
            periodType = dateFilterToPeriodType(budgetRequest.periodType),
            startDate = budgetRequest.startDate,
            endDate = budgetRequest.endDate,
            isRecurring = if (budgetRequest.isRecurring) 1L else 0L,
            alertEnabled = if (budgetRequest.alertEnabled) 1L else 0L,
            alertThresholdPercentage = budgetRequest.alertThresholdPercentage,
        )

        return dataSource.getLastInsertRowId()
    }

    private fun dateFilterToPeriodType(filter: DateFilter): String {
        return when (filter) {
            DateFilter.ThisWeek -> "WEEKLY"
            DateFilter.ThisMonth -> "MONTHLY"
            DateFilter.ThisYear -> "YEARLY"
            is DateFilter.Custom -> "CUSTOM"
        }
    }

    // Update existing budget
    suspend fun updateBudget(id: Long, budgetRequest: BudgetRequest) {
        dataSource.updateBudget(
            id = id,
            name = budgetRequest.name,
            categoryId = budgetRequest.categoryId,
            amount = budgetRequest.amount,
            periodType = dateFilterToPeriodType(budgetRequest.periodType),
            startDate = budgetRequest.startDate,
            endDate = budgetRequest.endDate,
            isRecurring = if (budgetRequest.isRecurring) 1L else 0L,
            alertEnabled = if (budgetRequest.alertEnabled) 1L else 0L,
            alertThresholdPercentage = budgetRequest.alertThresholdPercentage,
        )
    }

    // Delete budget (soft delete)
    suspend fun deleteBudget(id: Long) {
        dataSource.deleteBudget(id)
    }

    // Get budget with spending information for current period
    suspend fun getBudgetWithSpending(budgetId: Long, range: DateRange): BudgetWithSpending? {
        val budget = getBudgetById(budgetId) ?: return null

        val spentAmount = dataSource.getBudgetSpendingForPeriod(
            range.start.toEpochMillis(),
            range.end.toEpochMillis(),
            budgetId
        )

        return BudgetWithSpending(
            budget = budget,
            spentAmount = spentAmount
        )
    }

    // Get budget with spending for custom period
    suspend fun getBudgetWithSpendingForPeriod(
        budgetId: Long,
        startDate: Long,
        endDate: Long
    ): BudgetWithSpending? {
        val budget = getBudgetById(budgetId) ?: return null

        val spentAmount = dataSource.getBudgetSpendingForPeriod(
            startDate,
            endDate,
            budgetId
        )

        return BudgetWithSpending(
            budget = budget,
            spentAmount = spentAmount
        )
    }

    // Get all budgets with spending for current period — single JOIN query, no N+1
    fun getAllBudgetsWithSpending(range: DateRange): Flow<List<BudgetWithSpending>> {
        return dataSource.getAllActiveBudgetsWithSpending(
            startDate = range.start.toEpochMillis(),
            endDate = range.end.toEpochMillis()
        ).map { rows -> rows.map { it.toDomain() } }
    }

    // Get budgets relevant to a category (category-specific + overall) with spending — single query
    suspend fun getActiveBudgetsWithSpendingForCategory(
        categoryId: Long?,
        range: DateRange
    ): List<BudgetWithSpending> {
        return dataSource.getActiveBudgetsWithSpendingForCategory(
            startDate = range.start.toEpochMillis(),
            endDate = range.end.toEpochMillis(),
            categoryId = categoryId
        ).map { it.toDomain() }
    }

    // Get budget summary
    fun getBudgetSummary(range: DateRange): Flow<BudgetSummary> {
        return getAllBudgetsWithSpending(range).map { budgetsWithSpending ->
            BudgetSummary(
                totalBudgets = budgetsWithSpending.size,
                totalBudgetAmount = budgetsWithSpending.sumOf { it.budget.amount },
                totalSpent = budgetsWithSpending.sumOf { it.spentAmount },
                overBudgetCount = budgetsWithSpending.count { it.isOverBudget },
                budgetsWithSpending = budgetsWithSpending
            )
        }
    }

    @OptIn(ExperimentalTime::class)
    suspend fun checkBudgetExceeded(
        amount: Double,
        categoryId: Long,
        range: DateRange,
        transactionDate: Long = Clock.System.now().toEpochMilliseconds()
    ): List<BudgetWithSpending> {
        val exceededBudgets = mutableListOf<BudgetWithSpending>()

        val categoryBudgets = dataSource.selectBudgetsByCategory(categoryId)
        val overallBudgets = dataSource.selectOverallBudgetsList()
        val allRelevantBudgets = categoryBudgets + overallBudgets

        for (budgetEntity in allRelevantBudgets) {
            if (transactionDate >= range.start.toEpochMillis() && transactionDate <= range.end.toEpochMillis()) {
                val currentSpent = dataSource.getBudgetSpendingForPeriod(
                    range.start.toEpochMillis(),
                    range.end.toEpochMillis(),
                    budgetEntity.id
                )

                val projectedSpent = currentSpent + amount

                if (projectedSpent > budgetEntity.amount) {
                    exceededBudgets.add(
                        BudgetWithSpending(
                            budget = budgetEntity.toDomain(),
                            spentAmount = projectedSpent
                        )
                    )
                }
            }
        }

        return exceededBudgets
    }

    @OptIn(ExperimentalTime::class)
    suspend fun updateLastAlertTime(
        budgetId: Long,
        nowEpochSeconds: Long = Clock.System.now().epochSeconds
    ) {
        dataSource.updateLastAlertTime(nowEpochSeconds, budgetId)
    }
}