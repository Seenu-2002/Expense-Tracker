package com.ajay.seenu.expensetracker.android.presentation.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.android.data.FilterPreference
import com.ajay.seenu.expensetracker.android.data.getThisMonthInMillis
import com.ajay.seenu.expensetracker.android.data.getThisWeekInMillis
import com.ajay.seenu.expensetracker.android.data.getThisYearInMillis
import com.ajay.seenu.expensetracker.android.domain.data.Filter
import com.ajay.seenu.expensetracker.android.domain.data.TransactionsByDate
import com.ajay.seenu.expensetracker.android.domain.usecases.GetFilteredOverallDataUseCase
import com.ajay.seenu.expensetracker.android.domain.usecases.GetFilteredTransactionsUseCase
import com.ajay.seenu.expensetracker.android.domain.usecases.GetRecentTransactionsUseCase
import com.ajay.seenu.expensetracker.android.domain.usecases.transaction.DeleteTransactionUseCase
import com.ajay.seenu.expensetracker.android.presentation.widgets.OverallData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import javax.inject.Inject

@HiltViewModel
class OverviewScreenViewModel @Inject constructor(
    private val dateFormat: SimpleDateFormat
) :
    ViewModel() {

    private val _overallData: MutableStateFlow<OverallData?> = MutableStateFlow(null)
    val overallData = _overallData.asStateFlow()

    private val _recentTransactions: MutableStateFlow<List<TransactionsByDate>> =
        MutableStateFlow(emptyList())
    val recentTransactions = _recentTransactions.asStateFlow()

    private val _userName: MutableStateFlow<String> = MutableStateFlow("")
    val userName = _userName.asStateFlow()

    private val _hasMoreData: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val hasMoreData = _hasMoreData.asStateFlow()

    private val _currentFilter: MutableStateFlow<Filter> = MutableStateFlow(Filter.ThisMonth)
    val currentFilter: StateFlow<Filter> = _currentFilter.asStateFlow()

    @Inject
    internal lateinit var getRecentTransactions: GetRecentTransactionsUseCase

    @Inject
    internal lateinit var getFilteredTransactionsUseCase: GetFilteredTransactionsUseCase

    @Inject
    internal lateinit var getFilteredOverallDataUseCase: GetFilteredOverallDataUseCase

    @Inject
    internal lateinit var deleteTransactionUseCase: DeleteTransactionUseCase

    init {
        viewModelScope.launch {
            _userName.emit("Seenivasan T")
        }
    }

    private var lastFetchedPage: Int = 1

    private fun getOverallData(filter: Filter) {
        viewModelScope.launch {
            val range = when(filter) {
                Filter.ThisWeek -> {
                    getThisWeekInMillis()
                }
                Filter.ThisYear -> {
                    getThisYearInMillis()
                }
                Filter.ThisMonth -> {
                    getThisMonthInMillis()
                }
                is Filter.Custom -> {
                    filter.startDate to filter.endDate
                }
            }
            getFilteredOverallData(range.first, range.second)

        }
    }

    private fun getFilteredOverallData(fromValue: Long, toValue: Long) {
        viewModelScope.launch {
            getFilteredOverallDataUseCase.invoke(fromValue, toValue).collectLatest {
                _overallData.emit(it)
            }
        }
    }

    private fun getRecentTransactions(filter: Filter = Filter.ThisMonth) {
        viewModelScope.launch {
            lastFetchedPage = 1
            val range = when (filter) {
                Filter.ThisWeek -> {
                    getThisWeekInMillis()
                }
                Filter.ThisYear -> {
                    getThisYearInMillis()
                }
                Filter.ThisMonth -> {
                    getThisMonthInMillis()
                }
                is Filter.Custom -> {
                    filter.startDate to filter.endDate
                }
            }
            getFilteredTransactions(fromValue = range.first, toValue = range.second)
        }
    }

    private fun getFilteredTransactions(
        pageNo: Int = lastFetchedPage,
        fromValue: Long,
        toValue: Long
    ) {
        viewModelScope.launch {
            getFilteredTransactionsUseCase.invoke(pageNo, fromValue, toValue).collectLatest {
                _recentTransactions.emit(
                    if(lastFetchedPage == 1)
                        it.data
                    else
                        _recentTransactions.value + it.data
                )
                _hasMoreData.emit(it.hasMoreData)
            }
        }
    }

    fun getNextPageTransactions() {
        viewModelScope.launch {
            lastFetchedPage++
            val range = when (val filter = _currentFilter.value) {
                Filter.ThisWeek -> {
                    getThisWeekInMillis()
                }
                Filter.ThisYear -> {
                    getThisYearInMillis()
                }
                Filter.ThisMonth -> {
                    getThisMonthInMillis()
                }
                is Filter.Custom -> {
                    filter.startDate to filter.endDate
                }
            }
            getFilteredTransactions(fromValue = range.first, toValue = range.second)
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            deleteTransactionUseCase.invoke(id)
        }
    }

    fun setFilter(context: Context, filter: Filter) {
        FilterPreference.setCurrentFilter(context, filter)
        viewModelScope.launch {
            _currentFilter.emit(filter)
            getOverallData(filter)
            getRecentTransactions(filter)
        }
    }
}