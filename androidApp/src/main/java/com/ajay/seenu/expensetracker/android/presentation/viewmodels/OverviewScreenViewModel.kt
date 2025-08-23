package com.ajay.seenu.expensetracker.android.presentation.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.UserConfigurationsManager
import com.ajay.seenu.expensetracker.android.data.FilterPreference
import com.ajay.seenu.expensetracker.android.presentation.state.UiState
import com.ajay.seenu.expensetracker.domain.model.DateFilter
import com.ajay.seenu.expensetracker.domain.model.DateRange
import com.ajay.seenu.expensetracker.domain.model.OverallData
import com.ajay.seenu.expensetracker.domain.model.TransactionsByDate
import com.ajay.seenu.expensetracker.domain.usecase.DateRangeCalculatorUseCase
import com.ajay.seenu.expensetracker.domain.usecase.data_filter.GetFilteredOverallDataUseCase
import com.ajay.seenu.expensetracker.domain.usecase.data_filter.GetFilteredTransactionsUseCase
import com.ajay.seenu.expensetracker.domain.usecase.data_filter.GetRecentTransactionsUseCase
import com.ajay.seenu.expensetracker.domain.usecase.transaction.DeleteTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OverviewScreenViewModel @Inject constructor(
    private val userConfigurationsManager: UserConfigurationsManager
) : ViewModel() {

    private val _overallData: MutableStateFlow<UiState<OverallData>> =
        MutableStateFlow(UiState.Loading)
    val overallData = _overallData.asStateFlow()

    private val _recentTransactions: MutableStateFlow<UiState<List<TransactionsByDate>>> =
        MutableStateFlow(UiState.Loading)
    val recentTransactions = _recentTransactions.asStateFlow()

    private val _userName: MutableStateFlow<String> = MutableStateFlow("")
    val userName = _userName.asStateFlow()

    private val _hasMoreData: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val hasMoreData = _hasMoreData.asStateFlow()

    private val _currentFilter: MutableStateFlow<DateFilter> =
        MutableStateFlow(DateFilter.ThisMonth)
    val currentFilter: StateFlow<DateFilter> = _currentFilter.asStateFlow()

    private val _updatedDateFormat: MutableStateFlow<String> = MutableStateFlow("")
    val updatedDateFormat = _updatedDateFormat.asStateFlow()

    @Inject
    internal lateinit var getRecentTransactions: GetRecentTransactionsUseCase

    @Inject
    internal lateinit var getFilteredTransactionsUseCase: GetFilteredTransactionsUseCase

    @Inject
    internal lateinit var getFilteredOverallDataUseCase: GetFilteredOverallDataUseCase

    @Inject
    internal lateinit var deleteTransactionUseCase: DeleteTransactionUseCase

    @Inject
    internal lateinit var dateRangeCalculatorUseCase: DateRangeCalculatorUseCase

    init {
        init()
        viewModelScope.launch {
            _userName.emit("Seenivasan T")
        }
    }

    private var lastFetchedPage: Int = 1

    private fun getOverallData(filter: DateFilter) {
        viewModelScope.launch {
            val range = dateRangeCalculatorUseCase(filter)
            getFilteredOverallData(range)

        }
    }

    private fun init() {
        viewModelScope.launch {
            userConfigurationsManager.getDateFormat().collectLatest {
                _updatedDateFormat.emit(it)
            }
        }
    }

    private fun getFilteredOverallData(dateRange: DateRange) {
        viewModelScope.launch {
            _overallData.emit(UiState.Loading)
            getFilteredOverallDataUseCase.invoke(dateRange).collectLatest {
                _overallData.emit(UiState.Success(it))
            }
        }
    }

    private fun getRecentTransactions(filter: DateFilter = DateFilter.ThisMonth) {
        viewModelScope.launch {
            lastFetchedPage = 1
            val range = dateRangeCalculatorUseCase(filter)
            getFilteredTransactions(dateRange = range)
        }
    }

    private fun getFilteredTransactions(
        pageNo: Int = lastFetchedPage,
        dateRange: DateRange
    ) {
        viewModelScope.launch {
            val currentState = _recentTransactions.value
            _recentTransactions.emit(UiState.Loading)
            getFilteredTransactionsUseCase.invoke(dateRange = dateRange, pageNo = pageNo)
                .collectLatest {
                    _recentTransactions.emit(
                        UiState.Success(
                            if (lastFetchedPage == 1)
                                it.data
                            else
                                (currentState as UiState.Success).data + it.data
                        )
                    )
                    _hasMoreData.emit(it.hasMoreData)
                }
        }
    }

    fun getNextPageTransactions() {
        viewModelScope.launch {
            lastFetchedPage++
            val range = dateRangeCalculatorUseCase(_currentFilter.value)
            getFilteredTransactions(dateRange = range)
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            val currentFilter = _currentFilter.value
            deleteTransactionUseCase.invoke(id)
            getOverallData(currentFilter)
            getRecentTransactions(currentFilter)
        }
    }

    fun setFilter(context: Context, filter: DateFilter) {
        FilterPreference.setCurrentFilter(context, filter)
        viewModelScope.launch {
            _currentFilter.emit(filter)
            getOverallData(filter)
            getRecentTransactions(filter)
        }
    }
}