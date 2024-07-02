package com.ajay.seenu.expensetracker.android.presentation.viewmodels.chart_viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.android.domain.usecases.GetExpenseByCategoryUseCase
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseByCategoryChartViewModel @Inject constructor(
    private val getExpenseByCategory: GetExpenseByCategoryUseCase
) : ViewModel() {

    val modelProducer: CartesianChartModelProducer = CartesianChartModelProducer.build()
    val labelListKey = ExtraStore.Key<List<String>>()

    fun getData() {
        viewModelScope.launch {
            val data = getExpenseByCategory().associate {
                it.category to it.amount
            }
            modelProducer.tryRunTransaction {
                columnSeries {
                    series(data.values)
                }
                updateExtras { extraStore ->
                    extraStore[labelListKey] = data.keys.map { it.label }
                }
            }
        }
    }

}