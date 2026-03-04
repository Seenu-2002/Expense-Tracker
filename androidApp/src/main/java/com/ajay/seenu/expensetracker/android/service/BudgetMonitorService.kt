package com.ajay.seenu.expensetracker.android.service

import com.ajay.seenu.expensetracker.data.repository.BudgetRepository
import com.ajay.seenu.expensetracker.data.repository.TransactionRepository
import com.ajay.seenu.expensetracker.domain.model.DateRange
import com.ajay.seenu.expensetracker.domain.model.budget.Budget
import com.ajay.seenu.expensetracker.util.toEpochMillis
import javax.inject.Inject
import kotlin.time.ExperimentalTime

class BudgetMonitorService @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val budgetRepository: BudgetRepository,
    private val notificationService: NotificationService
) {

    suspend fun checkBudgetExceeded(transactionAmount: Double,
                                    categoryId: Long?,
                                    range: DateRange) {
        val categoryBudgets = if (categoryId != null) budgetRepository.getBudgetsByCategory(categoryId) else emptyList()
        val overallBudgets = budgetRepository.getOverallBudgetsList()
        val activeBudgets = categoryBudgets + overallBudgets
        activeBudgets.forEach { budget ->
            if (budget.alertEnabled) {
                val currentPeriodSpent = calculateCurrentPeriodSpent(budget, range)
                val spentRatio = if (budget.amount > 0) currentPeriodSpent / budget.amount else 0.0

                if (spentRatio >= budget.alertThresholdPercentage) {
                    triggerBudgetAlert(budget, spentRatio * 100, currentPeriodSpent)
                }
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun calculateCurrentPeriodSpent(budget: Budget, range: DateRange): Double {

        return transactionRepository.getTotalExpenseByCategoryInPeriod(
            categoryId = budget.categoryId,
            startDate = range.start.toEpochMillis(),
            endDate = range.end.toEpochMillis()
        )
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun triggerBudgetAlert(
        budget: Budget,
        percentage: Double,
        spentAmount: Double
    ) {
        // Check if we already alerted recently (e.g., within last 24 hours)
        val lastAlert = budget.lastAlertTriggeredAt
        val now = System.currentTimeMillis()
        val twentyFourHours = 24 * 60 * 60 * 1000L

        if (lastAlert != null && (now - lastAlert.toEpochMilliseconds()) < twentyFourHours) {
            return // Skip alert to avoid spam
        }

        // Send notification
        notificationService.sendBudgetAlert(budget, percentage, spentAmount)

        // Update last alert timestamp
        budgetRepository.updateLastAlertTime(budget.id, now)
    }
}