package com.ajay.seenu.expensetracker.android.presentation.viewmodels.chart_viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.UserConfigurationsManager
import com.ajay.seenu.expensetracker.android.util.getDateFormat
import com.ajay.seenu.expensetracker.android.presentation.screeens.charts.ChartState
import com.ajay.seenu.expensetracker.domain.model.DateFilter
import com.ajay.seenu.expensetracker.domain.usecase.DateRangeCalculatorUseCase
import com.ajay.seenu.expensetracker.domain.usecase.data_filter.GetTotalTransactionPerDayByCategoryUseCase
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TotalExpensePerDayChartViewModel @Inject constructor(
    private val useCase: GetTotalTransactionPerDayByCategoryUseCase,
    private val userConfigurationsManager: UserConfigurationsManager
) : ViewModel() {

    @Inject
    internal lateinit var dateRangeCalculatorUseCase: DateRangeCalculatorUseCase

    private val _chartState: MutableStateFlow<ChartState> = MutableStateFlow(ChartState.Empty)
    val chartState: StateFlow<ChartState> = _chartState.asStateFlow()

    val modelProducer: CartesianChartModelProducer = CartesianChartModelProducer.build()
    val labelListKey = ExtraStore.Key<List<String>>()
    private val _dataSetLabels: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    val dataSetLabels: StateFlow<List<String>> = _dataSetLabels.asStateFlow()

    private val _updatedDateFormat: MutableStateFlow<String> = MutableStateFlow("dd MMM, yyyy")
    val updatedDateFormat = _updatedDateFormat.asStateFlow()

    private lateinit var filter: DateFilter

    fun init() {
        viewModelScope.launch {
            userConfigurationsManager.getDateFormat().collectLatest {
                _updatedDateFormat.emit(it)
            }
        }
    }

    private fun getData(filter: DateFilter) {
        viewModelScope.launch {
            _chartState.emit(ChartState.Fetching)
            val data = async(Dispatchers.Default) {
                delay((0 .. 400L).random())
                val dateRange= dateRangeCalculatorUseCase(filter)
                useCase(dateRange)
            }.await()

            if (data.isEmpty()) {
                return@launch _chartState.emit(ChartState.Failed.InSufficientData)
            }

            _dataSetLabels.emit(data.first().expensePerCategory.map { it.category.label })
            val dateFormat = userConfigurationsManager.getConfigs().getDateFormat()
            modelProducer.tryRunTransaction {
                columnSeries {
                    val totalCategories = data.first().expensePerCategory.size
                    for (index in 0 until totalCategories) {
                        val series = data.map { it.expensePerCategory[index].amount }
                        series(series)
                    }
                }
                updateExtras { extraStore ->
                    extraStore[labelListKey] = data.map { dateFormat.format(it.date) }
                }
            }
            _chartState.emit(ChartState.Success)
        }
    }

    fun setFilter(filter: DateFilter) {
        this.filter = filter
        getData(filter)
    }

}