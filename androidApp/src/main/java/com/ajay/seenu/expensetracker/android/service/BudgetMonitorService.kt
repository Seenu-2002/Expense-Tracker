package com.ajay.seenu.expensetracker.android.service

import com.ajay.seenu.expensetracker.data.repository.BudgetRepository
import com.ajay.seenu.expensetracker.domain.model.DateRange
import com.ajay.seenu.expensetracker.domain.model.budget.Budget
import javax.inject.Inject
import kotlin.time.ExperimentalTime

class BudgetMonitorService @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val notificationService: NotificationService
) {

    // Single query fetches all relevant budgets + spending — no N+1
    suspend fun checkBudgetExceeded(transactionAmount: Double,
                                    categoryId: Long?,
                                    range: DateRange) {
        val budgetsWithSpending = budgetRepository.getActiveBudgetsWithSpendingForCategory(categoryId, range)
        budgetsWithSpending.forEach { budgetWithSpending ->
            val budget = budgetWithSpending.budget
            if (budget.alertEnabled) {
                val spentRatio = if (budget.amount > 0) budgetWithSpending.spentAmount / budget.amount else 0.0
                if (spentRatio >= budget.alertThresholdPercentage) {
                    triggerBudgetAlert(budget, spentRatio * 100, budgetWithSpending.spentAmount)
                }
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun triggerBudgetAlert(
        budget: Budget,
        percentage: Double,
        spentAmount: Double
    ) {
        // Check if we already alerted recently (e.g., within last 24 hours)
        val lastAlert = budget.lastAlertTriggeredAt
        val nowMillis = System.currentTimeMillis()
        val twentyFourHours = 24 * 60 * 60 * 1000L

        if (lastAlert != null && (nowMillis - lastAlert.toEpochMilliseconds()) < twentyFourHours) {
            return // Skip alert to avoid spam
        }

        // Send notification
        notificationService.sendBudgetAlert(budget, percentage, spentAmount)

        // Update last alert timestamp — store as epoch seconds (budget schema convention)
        budgetRepository.updateLastAlertTime(budget.id, nowMillis / 1000)
    }
}