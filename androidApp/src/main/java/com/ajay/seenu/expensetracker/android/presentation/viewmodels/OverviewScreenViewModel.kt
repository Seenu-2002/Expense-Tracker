package com.ajay.seenu.expensetracker.android.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class OverviewScreenViewModel : ViewModel() {

    private val _spendSoFar: MutableStateFlow<String> = MutableStateFlow("Rs. 1000")
    val spendSoFar = _spendSoFar.asStateFlow()

    private val _recentTransactions: MutableStateFlow<List<Transaction>> =
        MutableStateFlow(listOf())
    val recentTransactions = _recentTransactions.asStateFlow()

    private val _userName: MutableStateFlow<String> = MutableStateFlow("")
    val userName = _userName.asStateFlow()

    init {
        getRecentTransactions()
        viewModelScope.launch {
            _userName.emit("Seenivasan T")
        }
    }

    private fun getRecentTransactions() {
        viewModelScope.launch {
            val list = arrayListOf(
                Transaction(
                    Transaction.Type.INCOME,
                    "Test Transaction",
                    100.0,
                    "Netflix",
                    Date()
                ),
                Transaction(
                    Transaction.Type.INCOME,
                    "Test Transaction",
                    100.0,
                    "Netflix",
                    Date()
                ),
                Transaction(
                    Transaction.Type.EXPENSE,
                    "Test Transaction",
                    100.0,
                    "Netflix",
                    Date()
                ),
                Transaction(
                    Transaction.Type.INCOME,
                    "Test Transaction",
                    100.0,
                    "Netflix",
                    Date()
                ),
                Transaction(
                    Transaction.Type.INCOME,
                    "Test Transaction",
                    100.0,
                    "Netflix",
                    Date()
                ),
                Transaction(
                    Transaction.Type.EXPENSE,
                    "Test Transaction",
                    100.0,
                    "Netflix",
                    Date()
                )
            ).also { list ->
                repeat(100) {
                    list.add(list[it % list.size])
                }
            }
            _recentTransactions.emit(list)
        }
    }

}