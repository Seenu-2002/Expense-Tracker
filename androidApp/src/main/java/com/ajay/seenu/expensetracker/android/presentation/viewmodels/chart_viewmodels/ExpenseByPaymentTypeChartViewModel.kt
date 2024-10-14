package com.ajay.seenu.expensetracker.android.presentation.viewmodels.chart_viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.android.domain.data.Filter
import com.ajay.seenu.expensetracker.android.domain.usecases.GetExpensesByPaymentTypeUseCase
import com.ajay.seenu.expensetracker.android.presentation.screeens.charts.ChartState
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
internal class ExpenseByPaymentTypeChartViewModel @Inject constructor(
    private val getExpensesPerDayByPaymentType: GetExpensesByPaymentTypeUseCase,
) : ViewModel() {

    private val _chartState: MutableStateFlow<ChartState> = MutableStateFlow(ChartState.Empty)
    val chartState: StateFlow<ChartState> = _chartState.asStateFlow()

    val modelProducer: CartesianChartModelProducer = CartesianChartModelProducer.build()
    val labelListKey = ExtraStore.Key<List<String>>()
    private lateinit var filter: Filter

    private fun getData(filter: Filter) {
        viewModelScope.launch {
            _chartState.emit(ChartState.Fetching)
            val data = async (Dispatchers.Default) {
                delay((0 .. 400L).random())
                getExpensesPerDayByPaymentType(filter)
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

    fun setFilter(filter: Filter) {
        if (!this::filter.isInitialized || this.filter != filter) {
            this.filter = filter
            getData(filter)
        }
    }

}