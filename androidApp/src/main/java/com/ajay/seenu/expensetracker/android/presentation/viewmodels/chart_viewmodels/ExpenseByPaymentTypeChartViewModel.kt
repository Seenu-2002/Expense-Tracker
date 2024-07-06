package com.ajay.seenu.expensetracker.android.presentation.viewmodels.chart_viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.android.domain.usecases.GetExpensesByPaymentTypeUseCase
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class ExpenseByPaymentTypeChartViewModel @Inject constructor(
    private val getExpensesPerDayByPaymentType: GetExpensesByPaymentTypeUseCase,
) : ViewModel() {

    val modelProducer: CartesianChartModelProducer = CartesianChartModelProducer.build()
    val labelListKey = ExtraStore.Key<List<String>>()

    fun getData() {
        viewModelScope.launch {
            val data = getExpensesPerDayByPaymentType().associate {
                it.paymentType to (it.totalAmount ?: 0.0)
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