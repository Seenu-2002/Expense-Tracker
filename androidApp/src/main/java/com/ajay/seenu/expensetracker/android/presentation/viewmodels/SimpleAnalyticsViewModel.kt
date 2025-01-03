package com.ajay.seenu.expensetracker.android.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.android.domain.data.Error
import com.ajay.seenu.expensetracker.android.domain.data.Filter
import com.ajay.seenu.expensetracker.android.domain.data.Transaction
import com.ajay.seenu.expensetracker.android.domain.data.UiState
import com.ajay.seenu.expensetracker.android.domain.usecases.GetPieChartDataUseCase
import com.ajay.seenu.expensetracker.android.presentation.screeens.AnalyticsScreenData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SimpleAnalyticsViewModel @Inject constructor(
    private val getPieChartDataUseCase: GetPieChartDataUseCase
) : ViewModel() {

    private val _selectedType: MutableStateFlow<Transaction.Type> =
        MutableStateFlow(Transaction.Type.EXPENSE)
    val selectedType: StateFlow<Transaction.Type> = _selectedType.asStateFlow()

    private val _data: MutableStateFlow<UiState<AnalyticsScreenData>> = MutableStateFlow(UiState.Loading)
    val data: StateFlow<UiState<AnalyticsScreenData>> = _data.asStateFlow()

    fun changeType(type: Transaction.Type) {
        viewModelScope.launch {
            _selectedType.emit(type)
            getData(type)
        }
    }

    private suspend fun getData(type: Transaction.Type) {
        try {
            val data = getPieChartDataUseCase(
                type = type,
                Filter.ThisYear
            )
            if (_data.value == UiState.Loading) {
                delay(500L)
            }

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