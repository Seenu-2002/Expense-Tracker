package com.ajay.seenu.expensetracker

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.ajay.seenu.expensetracker.entity.budget.BudgetPeriod
import com.ajay.seenu.expensetracker.entity.budget.BudgetPeriodType
import com.ajay.seenu.expensetracker.entity.budget.BudgetRequest
import com.ajay.seenu.expensetracker.entity.budget.BudgetSummary
import com.ajay.seenu.expensetracker.entity.budget.BudgetWithSpending
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

class BudgetRepository(
    private val database: ExpenseDatabase
) {

    // Get all active budgets
    fun getAllActiveBudgets(): Flow<List<Budget>> {
        return database.expenseDatabaseQueries.selectAllActiveBudgets()
            .asFlow()
            .mapToList(Dispatchers.IO)
    }

    // Get budgets by category
    fun getBudgetsByCategory(categoryId: Long): Flow<List<Budget>> {
        return database.expenseDatabaseQueries.selectBudgetsByCategory(categoryId)
            .asFlow()
            .mapToList(Dispatchers.IO)
    }

    // Get overall budgets (not tied to specific categories)
    fun getOverallBudgets(): Flow<List<Budget>> {
        return database.expenseDatabaseQueries.selectOverallBudgets()
            .asFlow()
            .mapToList(Dispatchers.IO)
    }

    // Get budget by ID
    suspend fun getBudgetById(id: Long): Budget? {
        return database.expenseDatabaseQueries.selectBudgetById(id).executeAsOneOrNull()
    }

    // Create new budget
    suspend fun createBudget(budgetRequest: BudgetRequest): Long {
        database.expenseDatabaseQueries.insertBudget(
            name = budgetRequest.name,
            categoryId = budgetRequest.categoryId,
            amount = budgetRequest.amount,
            periodType = budgetRequest.periodType.name,
            startDate = budgetRequest.startDate,
            endDate = budgetRequest.endDate,
            isRecurring = if (budgetRequest.isRecurring) 1L else 0L
        )

        return database.expenseDatabaseQueries.getLastInsertTransactionRowId().executeAsOne()
    }

    // Update existing budget
    suspend fun updateBudget(id: Long, budgetRequest: BudgetRequest) {
        database.expenseDatabaseQueries.updateBudget(
            name = budgetRequest.name,
            categoryId = budgetRequest.categoryId,
            amount = budgetRequest.amount,
            periodType = budgetRequest.periodType.name,
            startDate = budgetRequest.startDate,
            endDate = budgetRequest.endDate,
            isRecurring = if (budgetRequest.isRecurring) 1L else 0L,
            id = id
        )
    }

    // Delete budget (soft delete)
    suspend fun deleteBudget(id: Long) {
        database.expenseDatabaseQueries.deleteBudget(id)
    }

    // Get budget with spending information for current period
    suspend fun getBudgetWithSpending(budgetId: Long): BudgetWithSpending? {
        val budget = getBudgetById(budgetId) ?: return null
        val period = BudgetPeriod.getCurrentPeriod(BudgetPeriodType.valueOf(budget.periodType))

        val spentAmount = database.expenseDatabaseQueries.getBudgetSpendingForPeriod(
            period.startDate,
            period.endDate,
            budgetId
        ).executeAsOneOrNull()?.COALESCE ?: 0.0

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

        val spentAmount = database.expenseDatabaseQueries.getBudgetSpendingForPeriod(
            startDate,
            endDate,
            budgetId
        ).executeAsOneOrNull()?.COALESCE ?: 0.0

        return BudgetWithSpending(
            budget = budget,
            spentAmount = spentAmount
        )
    }

    // Get all budgets with spending for current period
    fun getAllBudgetsWithSpending(): Flow<List<BudgetWithSpending>> {
        return getAllActiveBudgets().map { budgets ->
            budgets.map { budget ->
                val period = BudgetPeriod.getCurrentPeriod(BudgetPeriodType.valueOf(budget.periodType))
                val spentAmount = database.expenseDatabaseQueries.getBudgetSpendingForPeriod(
                    period.startDate,
                    period.endDate,
                    budget.id
                ).executeAsOneOrNull()?.COALESCE ?: 0.0

                BudgetWithSpending(
                    budget = budget,
                    spentAmount = spentAmount
                )
            }
        }
    }

    // Get budget summary
    fun getBudgetSummary(): Flow<BudgetSummary> {
        return getAllBudgetsWithSpending().map { budgetsWithSpending ->
            BudgetSummary(
                totalBudgets = budgetsWithSpending.size,
                totalBudgetAmount = budgetsWithSpending.sumOf { it.budget.amount },
                totalSpent = budgetsWithSpending.sumOf { it.spentAmount },
                overBudgetCount = budgetsWithSpending.count { it.isOverBudget },
                budgetsWithSpending = budgetsWithSpending
            )
        }
    }

    suspend fun checkBudgetExceeded(
        amount: Double,
        categoryId: Long,
        transactionDate: Long = Clock.System.now().toEpochMilliseconds()
    ): List<BudgetWithSpending> {
        val exceededBudgets = mutableListOf<BudgetWithSpending>()

        // Check category-specific budgets
        val categoryBudgets = database.expenseDatabaseQueries.selectBudgetsByCategory(categoryId)
            .executeAsList()

        // Check overall budgets
        val overallBudgets = database.expenseDatabaseQueries.selectOverallBudgets()
            .executeAsList()

        val allRelevantBudgets = categoryBudgets + overallBudgets

        for (budget in allRelevantBudgets) {
            val period = if (budget.periodType == BudgetPeriodType.CUSTOM.name && budget.endDate != null) {
                BudgetPeriod(budget.startDate, budget.endDate!!)
            } else {
                BudgetPeriod.getCurrentPeriod(BudgetPeriodType.valueOf(budget.periodType))
            }

            // Check if transaction date falls within budget period
            if (transactionDate >= period.startDate && transactionDate <= period.endDate) {
                val currentSpent = database.expenseDatabaseQueries.getBudgetSpendingForPeriod(
                    period.startDate,
                    period.endDate,
                    budget.id
                ).executeAsOneOrNull()?.COALESCE ?: 0.0

                val projectedSpent = currentSpent + amount

                if (projectedSpent > budget.amount) {
                    exceededBudgets.add(
                        BudgetWithSpending(
                            budget = budget,
                            spentAmount = projectedSpent
                        )
                    )
                }
            }
        }

        return exceededBudgets
    }
}