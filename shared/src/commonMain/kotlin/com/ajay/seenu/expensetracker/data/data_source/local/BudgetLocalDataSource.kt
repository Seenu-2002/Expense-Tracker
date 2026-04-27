package com.ajay.seenu.expensetracker.data.data_source.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.ajay.seenu.expensetracker.BudgetEntity
import com.ajay.seenu.expensetracker.ExpenseDatabase
import com.ajay.seenu.expensetracker.GetActiveBudgetsWithSpendingForCategory
import com.ajay.seenu.expensetracker.GetAllActiveBudgetsWithSpending
import com.ajay.seenu.expensetracker.data.data_source.BudgetDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow

class BudgetLocalDataSource(
    database: ExpenseDatabase
) : BudgetDataSource {

    private val queries = database.expenseDatabaseQueries

    override fun selectAllActiveBudgets(): Flow<List<BudgetEntity>> {
        return queries.selectAllActiveBudgets().asFlow().mapToList(Dispatchers.IO)
    }

    override fun selectBudgetsByCategory(categoryId: Long?): List<BudgetEntity> {
        return queries.selectBudgetsByCategory(categoryId).executeAsList()
    }

    override fun selectOverallBudgets(): Flow<List<BudgetEntity>> {
        return queries.selectOverallBudgets().asFlow().mapToList(Dispatchers.IO)
    }

    override fun selectOverallBudgetsList(): List<BudgetEntity> {
        return queries.selectOverallBudgets().executeAsList()
    }

    override fun selectBudgetById(id: Long): BudgetEntity? {
        return queries.selectBudgetById(id).executeAsOneOrNull()
    }

    override fun insertBudget(
        name: String,
        categoryId: Long?,
        amount: Double,
        periodType: String,
        startDate: Long,
        endDate: Long?,
        isRecurring: Long,
        alertEnabled: Long,
        alertThresholdPercentage: Double
    ) {
        queries.insertBudget(
            name = name,
            categoryId = categoryId,
            amount = amount,
            periodType = periodType,
            startDate = startDate,
            endDate = endDate,
            isRecurring = isRecurring,
            alertEnabled = alertEnabled,
            alertThresholdPercentage = alertThresholdPercentage
        )
    }

    override fun getLastInsertRowId(): Long {
        return queries.getLastInsertTransactionRowId().executeAsOne()
    }

    override fun updateBudget(
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
    ) {
        queries.updateBudget(
            name = name,
            categoryId = categoryId,
            amount = amount,
            periodType = periodType,
            startDate = startDate,
            endDate = endDate,
            isRecurring = isRecurring,
            id = id,
            alertEnabled = alertEnabled,
            alertThresholdPercentage = alertThresholdPercentage
        )
    }

    override fun deleteBudget(id: Long) {
        queries.deleteBudget(id)
    }

    override fun getBudgetSpendingForPeriod(
        startDate: Long,
        endDate: Long,
        budgetId: Long
    ): Double {
        return queries.getBudgetSpendingForPeriod(startDate, endDate, budgetId)
            .executeAsOneOrNull()?.COALESCE ?: 0.0
    }

    override fun getAllActiveBudgetsWithSpending(
        startDate: Long,
        endDate: Long
    ): Flow<List<GetAllActiveBudgetsWithSpending>> {
        return queries.getAllActiveBudgetsWithSpending(startDate, endDate)
            .asFlow()
            .mapToList(Dispatchers.IO)
    }

    override fun getActiveBudgetsWithSpendingForCategory(
        startDate: Long,
        endDate: Long,
        categoryId: Long?
    ): List<GetActiveBudgetsWithSpendingForCategory> {
        return queries.getActiveBudgetsWithSpendingForCategory(startDate, endDate, categoryId)
            .executeAsList()
    }

    override fun updateLastAlertTime(epochSeconds: Long, budgetId: Long) {
        queries.updateLastAlertTime(epochSeconds, budgetId)
    }
}

