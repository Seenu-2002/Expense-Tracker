package com.ajay.seenu.expensetracker.android.presentation.viewmodels.chart_viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.domain.model.DateFilter
import com.ajay.seenu.expensetracker.domain.usecase.DateRangeCalculatorUseCase
import com.ajay.seenu.expensetracker.domain.usecase.data_filter.GetFilteredOverallDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SummaryKpi(
    val income: Double,
    val expense: Double,
) {
    val net: Double get() = income - expense
    val savingsRate: Double get() = if (income > 0.0) (net / income) * 100.0 else 0.0
}

@HiltViewModel
class SummaryKpiViewModel @Inject constructor(
    private val getOverallData: GetFilteredOverallDataUseCase,
    private val dateRangeCalculator: DateRangeCalculatorUseCase,
) : ViewModel() {

    private val _kpi: MutableStateFlow<SummaryKpi> = MutableStateFlow(SummaryKpi(0.0, 0.0))
    val kpi: StateFlow<SummaryKpi> = _kpi.asStateFlow()

    private var currentFilter: DateFilter? = null

    fun setFilter(filter: DateFilter) {
        if (currentFilter == filter) return
        currentFilter = filter
        viewModelScope.launch {
            val dateRange = dateRangeCalculator(filter)
            getOverallData(dateRange).collectLatest { data ->
                _kpi.emit(SummaryKpi(income = data.income, expense = data.expense))
            }
        }
    }
}
