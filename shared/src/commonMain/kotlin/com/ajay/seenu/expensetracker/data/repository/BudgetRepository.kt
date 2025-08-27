package com.ajay.seenu.expensetracker.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.ajay.seenu.expensetracker.ExpenseDatabase
import com.ajay.seenu.expensetracker.data.mapper.toDomain
import com.ajay.seenu.expensetracker.domain.model.budget.Budget
import com.ajay.seenu.expensetracker.domain.model.budget.BudgetPeriod
import com.ajay.seenu.expensetracker.domain.model.budget.BudgetPeriodType
import com.ajay.seenu.expensetracker.domain.model.budget.BudgetRequest
import com.ajay.seenu.expensetracker.domain.model.budget.BudgetSummary
import com.ajay.seenu.expensetracker.domain.model.budget.BudgetWithSpending
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class BudgetRepository(
    private val database: ExpenseDatabase
) {

    // Get all active budgets
    fun getAllActiveBudgets(): Flow<List<Budget>> {
        return database.expenseDatabaseQueries.selectAllActiveBudgets()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { budgets ->
                budgets.map { it.toDomain() }
            }
    }

    // Get budgets by category
    fun getBudgetsByCategory(categoryId: Long): Flow<List<Budget>> {
        return database.expenseDatabaseQueries.selectBudgetsByCategory(categoryId)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { budgets ->
                budgets.map { it.toDomain() }
            }
    }

    // Get overall budgets (not tied to specific categories)
    fun getOverallBudgets(): Flow<List<Budget>> {
        return database.expenseDatabaseQueries.selectOverallBudgets()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { budgets ->
                budgets.map { it.toDomain() }
            }
    }

    // Get budget by ID
    suspend fun getBudgetById(id: Long): Budget? {
        return database.expenseDatabaseQueries.selectBudgetById(id).executeAsOneOrNull()
            ?.toDomain()
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

    @OptIn(ExperimentalTime::class)
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

        for (budgetEntity in allRelevantBudgets) {
            val period = if (budgetEntity.periodType == BudgetPeriodType.CUSTOM.name && budgetEntity.endDate != null) {
                BudgetPeriod(budgetEntity.startDate, budgetEntity.endDate!!)
            } else {
                BudgetPeriod.getCurrentPeriod(BudgetPeriodType.valueOf(budgetEntity.periodType))
            }

            // Check if transaction date falls within budgetEntity period
            if (transactionDate >= period.startDate && transactionDate <= period.endDate) {
                val currentSpent = database.expenseDatabaseQueries.getBudgetSpendingForPeriod(
                    period.startDate,
                    period.endDate,
                    budgetEntity.id
                ).executeAsOneOrNull()?.COALESCE ?: 0.0

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
}