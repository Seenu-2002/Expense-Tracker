package com.ajay.seenu.expensetracker.android.presentation.viewmodels.chart_viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.android.presentation.screeens.charts.ChartState
import com.ajay.seenu.expensetracker.domain.model.DateFilter
import com.ajay.seenu.expensetracker.domain.usecase.DateRangeCalculatorUseCase
import com.ajay.seenu.expensetracker.domain.usecase.data_filter.GetIncomeAndExpensePerDayUseCase
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.time.ExperimentalTime

@HiltViewModel
class IncomeExpenseTrendViewModel @Inject constructor(
    private val getIncomeAndExpensePerDay: GetIncomeAndExpensePerDayUseCase,
    private val dateRangeCalculator: DateRangeCalculatorUseCase,
) : ViewModel() {

    private val _chartState: MutableStateFlow<ChartState> = MutableStateFlow(ChartState.Empty)
    val chartState: StateFlow<ChartState> = _chartState.asStateFlow()

    val modelProducer: CartesianChartModelProducer = CartesianChartModelProducer.build()
    val xLabelsKey = ExtraStore.Key<List<String>>()

    private var currentFilter: DateFilter? = null

    fun setFilter(filter: DateFilter) {
        if (currentFilter == filter) return
        currentFilter = filter
        loadData(filter)
    }

    @OptIn(ExperimentalTime::class)
    private fun loadData(filter: DateFilter) {
        viewModelScope.launch {
            _chartState.emit(ChartState.Fetching)
            val result = withContext(Dispatchers.Default) {
                val dateRange = dateRangeCalculator(filter)
                getIncomeAndExpensePerDay(dateRange)
            }

            if (result.isEmpty()) {
                _chartState.emit(ChartState.Failed.InSufficientData)
                return@launch
            }

            val isMonthly = result.size > 1 &&
                result.zipWithNext().any { (a, b) -> a.date.monthNumber != b.date.monthNumber || a.date.year != b.date.year } &&
                result.size <= 24 &&
                result.all { it.date.dayOfMonth == 1 }
            val labelFormatter = SimpleDateFormat(
                if (isMonthly) "MMM yyyy" else "MMM d",
                Locale.ENGLISH,
            )
            val incomes = result.map { it.income }
            val expenses = result.map { it.expense }
            val labels = result.map { labelFormatter.format(it.date.toJavaDate()) }

            modelProducer.tryRunTransaction {
                lineSeries {
                    series(incomes)
                    series(expenses)
                }
                updateExtras { extraStore ->
                    extraStore[xLabelsKey] = labels
                }
            }
            _chartState.emit(ChartState.Success)
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun LocalDate.toJavaDate(): Date {
        val instant = this.atStartOfDayIn(TimeZone.currentSystemDefault())
        return Date(instant.toEpochMilliseconds())
    }
}
