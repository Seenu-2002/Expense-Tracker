package com.ajay.seenu.expensetracker.android.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class AddTransactionViewModel : ViewModel() {

    private val _transaction: MutableStateFlow<Transaction?> = MutableStateFlow(null)
    val transaction = _transaction.asStateFlow()

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            _transaction.emit(transaction)
        }
    }

}

// FIXME: Move to separate package
class Transaction constructor(
    val title: String,
    val type: Type,
    val description: String,
    val amount: Double,
    val category: String, // TODO("Sealed class")
    val date: Date
) {

    enum class Type {
        EXPENSE, INCOME
    }

}