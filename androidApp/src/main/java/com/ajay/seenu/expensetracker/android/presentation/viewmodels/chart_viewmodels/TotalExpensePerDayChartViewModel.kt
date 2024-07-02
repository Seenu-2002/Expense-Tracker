package com.ajay.seenu.expensetracker.android.presentation.viewmodels.chart_viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.android.domain.usecases.GetTotalTransactionPerDayByCategoryUseCase
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import javax.inject.Inject

@HiltViewModel
class TotalExpensePerDayChartViewModel @Inject constructor(
    private val useCase: GetTotalTransactionPerDayByCategoryUseCase,
    private val dateFormat: SimpleDateFormat
) : ViewModel() {

    val modelProducer: CartesianChartModelProducer = CartesianChartModelProducer.build()
    val labelListKey = ExtraStore.Key<List<String>>()

    fun getData() {
        viewModelScope.launch {
            val data = useCase()
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
        }
    }

}