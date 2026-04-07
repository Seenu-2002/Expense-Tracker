package com.ajay.seenu.expensetracker.android.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.android.domain.usecases.GetPieChartDataUseCase
import com.ajay.seenu.expensetracker.android.presentation.screeens.AnalyticsScreenData
import com.ajay.seenu.expensetracker.android.presentation.state.Error
import com.ajay.seenu.expensetracker.android.presentation.state.UiState
import com.ajay.seenu.expensetracker.domain.model.DateFilter
import com.ajay.seenu.expensetracker.domain.model.TransactionType
import com.ajay.seenu.expensetracker.domain.usecase.DateRangeCalculatorUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SimpleAnalyticsViewModel @Inject constructor(
    private val getPieChartDataUseCase: GetPieChartDataUseCase
) : ViewModel() {

    @Inject
    internal lateinit var dateRangeCalculatorUseCase: DateRangeCalculatorUseCase

    private val _selectedType: MutableStateFlow<TransactionType> =
        MutableStateFlow(TransactionType.EXPENSE)
    val selectedType: StateFlow<TransactionType> = _selectedType.asStateFlow()

    private val _data: MutableStateFlow<UiState<AnalyticsScreenData>> = MutableStateFlow(UiState.Loading)
    val data: StateFlow<UiState<AnalyticsScreenData>> = _data.asStateFlow()

    fun changeType(type: TransactionType, filter: DateFilter) {
        viewModelScope.launch {
            _selectedType.emit(type)
            getData(type, filter)
        }
    }

    private suspend fun getData(type: TransactionType, filter: DateFilter) {
        try {
            val dateRange = dateRangeCalculatorUseCase(filter)
            val data = getPieChartDataUseCase(
                type = type,
                dateRange
            )

            if (data.chartData.entries.isEmpty()) {
                _data.emit(UiState.Failure(Error.Empty))
            } else {
                _data.emit(UiState.Success(data))
            }
        } catch (exp: Throwable) {
            _data.emit(UiState.Failure(Error.Unhandled(exp)))
        }
    }

}