package com.ajay.seenu.expensetracker.android.presentation.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.android.data.FilterPreference
import com.ajay.seenu.expensetracker.domain.model.DateFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    val chartOrder: List<Charts> = listOf(
        Charts.SUMMARY_KPI,
        Charts.TOP_SPENDING_CATEGORIES,
        Charts.EXPENSE_BY_CATEGORY_DONUT,
        Charts.BUDGET_HEALTH,
        Charts.INCOME_EXPENSE_TREND,
        Charts.TOTAL_EXPENSE_PER_DAY_BY_CATEGORY,
    )

    private val _currentFilter: MutableStateFlow<DateFilter> = MutableStateFlow(DateFilter.ThisMonth)
    val currentFilter: StateFlow<DateFilter> = _currentFilter.asStateFlow()

    private val _updatedDateFormat: MutableStateFlow<String> = MutableStateFlow("dd MMM yyyy")

    fun setFilter(filter: DateFilter) {
        FilterPreference.setCurrentFilter(context, filter)
        viewModelScope.launch {
            _currentFilter.emit(filter)
        }
    }

    fun getDateFormatter(): SimpleDateFormat {
        return SimpleDateFormat(_updatedDateFormat.value, Locale.ENGLISH)
    }
}

enum class Charts(val label: String) {
    SUMMARY_KPI("Summary"),
    EXPENSE_BY_CATEGORY_DONUT("Expense by Category"),
    INCOME_EXPENSE_TREND("Income vs Expense"),
    TOP_SPENDING_CATEGORIES("Top Spending Categories"),
    BUDGET_HEALTH("Budget Health"),
    TOTAL_EXPENSE_PER_DAY_BY_CATEGORY("Total Expense per day By Category"),
    EXPENSE_BY_CATEGORY("Expense by Category (bars)")
}