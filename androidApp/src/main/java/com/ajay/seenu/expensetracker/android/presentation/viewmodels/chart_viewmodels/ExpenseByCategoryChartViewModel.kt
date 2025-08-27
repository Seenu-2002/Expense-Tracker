package com.ajay.seenu.expensetracker.android.presentation.viewmodels.chart_viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.android.presentation.screeens.charts.ChartState
import com.ajay.seenu.expensetracker.domain.model.DateFilter
import com.ajay.seenu.expensetracker.domain.usecase.DateRangeCalculatorUseCase
import com.ajay.seenu.expensetracker.domain.usecase.data_filter.GetExpenseByCategoryUseCase
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseByCategoryChartViewModel @Inject constructor(
    private val getExpenseByCategory: GetExpenseByCategoryUseCase
) : ViewModel() {

    @Inject
    internal lateinit var dateRangeCalculatorUseCase: DateRangeCalculatorUseCase

    private val _chartState: MutableStateFlow<ChartState> = MutableStateFlow(ChartState.Empty)
    val chartState: StateFlow<ChartState> = _chartState.asStateFlow()

    val modelProducer: CartesianChartModelProducer = CartesianChartModelProducer.build()
    val labelListKey = ExtraStore.Key<List<String>>()
    private lateinit var filter: DateFilter

    fun setFilter(filter: DateFilter) {
        if (!this::filter.isInitialized || this.filter != filter) {
            this.filter = filter
            getData(filter)
        }
    }

    private fun getData(filter: DateFilter) {
        viewModelScope.launch {
            _chartState.emit(ChartState.Fetching)
            val data = async (Dispatchers.Default) {
                delay((0 .. 400L).random())
                val dateRange = dateRangeCalculatorUseCase(filter)
                getExpenseByCategory(dateRange).associate {
                    it.category to it.amount
                }
            }.await()

            if (data.isEmpty()) {
                return@launch _chartState.emit(ChartState.Failed.InSufficientData)
            }

            modelProducer.tryRunTransaction {
                columnSeries {
                    series(data.values)
                }
                updateExtras { extraStore ->
                    extraStore[labelListKey] = data.keys.map { it.label }
                }
            }
            _chartState.emit(ChartState.Success)
        }
    }

}