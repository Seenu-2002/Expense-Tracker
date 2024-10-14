package com.ajay.seenu.expensetracker.android.presentation.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.android.data.FilterPreference
import com.ajay.seenu.expensetracker.android.domain.data.Filter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor() : ViewModel() {

    val chartOrder: List<Charts> = Charts.entries

    private val _currentFilter: MutableStateFlow<Filter> = MutableStateFlow(Filter.ThisMonth)
    val currentFilter: StateFlow<Filter> = _currentFilter.asStateFlow()

    fun setFilter(context: Context, filter: Filter) {
        FilterPreference.setCurrentFilter(context, filter)
        viewModelScope.launch {
            _currentFilter.emit(filter)
        }
    }
}

enum class Charts(val label: String) {
    TOTAL_EXPENSE_PER_DAY_BY_CATEGORY("Total Expense per day By Category"),
    EXPENSE_BY_CATEGORY("Expense by Category"),
    EXPENSE_BY_PAYMENT_TYPE("Expense by Payment Type")
}