package com.ajay.seenu.expensetracker.android.presentation.viewmodels.chart_viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.domain.model.Category
import com.ajay.seenu.expensetracker.domain.model.DateFilter
import com.ajay.seenu.expensetracker.domain.usecase.DateRangeCalculatorUseCase
import com.ajay.seenu.expensetracker.domain.usecase.data_filter.GetExpenseByCategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class RankedCategory(
    val category: Category,
    val amount: Double,
    val percentOfTotal: Double,
)

sealed interface TopCategoriesState {
    data object Loading : TopCategoriesState
    data object Empty : TopCategoriesState
    data class Ready(val rows: List<RankedCategory>) : TopCategoriesState
}

@HiltViewModel
class TopSpendingCategoriesViewModel @Inject constructor(
    private val getExpenseByCategory: GetExpenseByCategoryUseCase,
    private val dateRangeCalculator: DateRangeCalculatorUseCase,
) : ViewModel() {

    private val _state: MutableStateFlow<TopCategoriesState> = MutableStateFlow(TopCategoriesState.Loading)
    val state: StateFlow<TopCategoriesState> = _state.asStateFlow()

    private var currentFilter: DateFilter? = null

    fun setFilter(filter: DateFilter) {
        if (currentFilter == filter) return
        currentFilter = filter
        load(filter)
    }

    private fun load(filter: DateFilter) {
        viewModelScope.launch {
            _state.emit(TopCategoriesState.Loading)
            val rows = withContext(Dispatchers.Default) {
                val dateRange = dateRangeCalculator(filter)
                val all = getExpenseByCategory(dateRange)
                if (all.isEmpty()) return@withContext emptyList<RankedCategory>()
                val total = all.sumOf { it.amount }
                all
                    .filter { it.amount > 0.0 }
                    .sortedByDescending { it.amount }
                    .take(TOP_N)
                    .map { item ->
                        RankedCategory(
                            category = item.category,
                            amount = item.amount,
                            percentOfTotal = if (total > 0.0) (item.amount / total) * 100.0 else 0.0,
                        )
                    }
            }
            _state.emit(if (rows.isEmpty()) TopCategoriesState.Empty else TopCategoriesState.Ready(rows))
        }
    }

    companion object {
        private const val TOP_N = 5
    }
}
