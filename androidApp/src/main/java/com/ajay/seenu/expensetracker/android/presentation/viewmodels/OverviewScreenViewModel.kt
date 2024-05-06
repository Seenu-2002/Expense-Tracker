package com.ajay.seenu.expensetracker.android.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.TransactionDetail
import com.ajay.seenu.expensetracker.android.domain.data.Transaction
import com.ajay.seenu.expensetracker.android.domain.usecases.GetOverallDataUseCase
import com.ajay.seenu.expensetracker.android.domain.usecases.GetRecentTransactionsUseCase
import com.ajay.seenu.expensetracker.android.domain.usecases.TransactionsByDate
import com.ajay.seenu.expensetracker.android.presentation.widgets.OverallData
import com.ajay.seenu.expensetracker.entity.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.HashMap
import java.util.Locale
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

    @Inject
    internal lateinit var getRecentTransactions: GetRecentTransactionsUseCase

    @Inject
    internal lateinit var getOverallDataUseCase: GetOverallDataUseCase

    init {
        viewModelScope.launch {
            _userName.emit("Seenivasan T")
        }
    }

    private var lastFetchedPage: Int = 1

    fun getOverallData() {
        viewModelScope.launch {
            getOverallDataUseCase().collectLatest {
                _overallData.emit(it)
            }
        }
    }

    fun getRecentTransactions() {
        viewModelScope.launch {
            lastFetchedPage = 1
            _recentTransactions.emit(emptyList())
            getTransactions()
        }
    }

    private suspend fun getTransactions(pageNo: Int = lastFetchedPage) {
        getRecentTransactions.invoke(pageNo).collectLatest { // FIXME: PAGINATION
            _recentTransactions.emit(_recentTransactions.value + it.data)
            _hasMoreData.emit(it.hasMoreData)
        }
    }

    fun getNextPageTransactions() {
        viewModelScope.launch {
            lastFetchedPage++
            getTransactions()
        }
    }
}