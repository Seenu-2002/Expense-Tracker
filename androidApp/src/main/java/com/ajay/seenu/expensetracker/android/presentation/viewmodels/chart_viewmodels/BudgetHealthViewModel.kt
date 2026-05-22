package com.ajay.seenu.expensetracker.android.presentation.viewmodels.chart_viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.data.repository.BudgetRepository
import com.ajay.seenu.expensetracker.domain.model.DateFilter
import com.ajay.seenu.expensetracker.domain.model.budget.BudgetWithSpending
import com.ajay.seenu.expensetracker.domain.usecase.DateRangeCalculatorUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BudgetHealth(
    val totalBudgeted: Double,
    val totalSpent: Double,
    val overallUsedPercent: Double,
    val atRiskCount: Int,
    val overBudgetCount: Int,
    val topOffenders: List<BudgetWithSpending>,
    val totalBudgets: Int,
) {
    val totalRemaining: Double get() = totalBudgeted - totalSpent
    val hasBudgets: Boolean get() = totalBudgets > 0
}

@HiltViewModel
class BudgetHealthViewModel @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val dateRangeCalculator: DateRangeCalculatorUseCase,
) : ViewModel() {

    private val _health: MutableStateFlow<BudgetHealth> = MutableStateFlow(EMPTY)
    val health: StateFlow<BudgetHealth> = _health.asStateFlow()

    private var currentFilter: DateFilter? = null

    fun setFilter(filter: DateFilter) {
        if (currentFilter == filter) return
        currentFilter = filter
        viewModelScope.launch {
            val range = dateRangeCalculator(filter)
            budgetRepository.getAllBudgetsWithSpending(range).collectLatest { budgets ->
                _health.emit(aggregate(budgets))
            }
        }
    }

    private fun aggregate(budgets: List<BudgetWithSpending>): BudgetHealth {
        if (budgets.isEmpty()) return EMPTY
        val totalBudgeted = budgets.sumOf { it.budget.amount }
        val totalSpent = budgets.sumOf { it.spentAmount }
        val overall = if (totalBudgeted > 0.0) (totalSpent / totalBudgeted) * 100.0 else 0.0
        val overBudget = budgets.count { it.isOverBudget }
        val atRisk = budgets.count { !it.isOverBudget && it.percentageUsed >= it.budget.alertThresholdPercentage * 100.0 }
        val offenders = budgets
            .sortedByDescending { it.percentageUsed }
            .take(TOP_OFFENDERS)
            .filter { it.percentageUsed >= MIN_DISPLAY_PERCENT }
        return BudgetHealth(
            totalBudgeted = totalBudgeted,
            totalSpent = totalSpent,
            overallUsedPercent = overall,
            atRiskCount = atRisk,
            overBudgetCount = overBudget,
            topOffenders = offenders,
            totalBudgets = budgets.size,
        )
    }

    companion object {
        private const val TOP_OFFENDERS = 3
        private const val MIN_DISPLAY_PERCENT = 50.0
        private val EMPTY = BudgetHealth(0.0, 0.0, 0.0, 0, 0, emptyList(), 0)
    }
}
