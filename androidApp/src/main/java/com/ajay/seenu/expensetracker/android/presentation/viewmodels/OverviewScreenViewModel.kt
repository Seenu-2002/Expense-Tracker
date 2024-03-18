package com.ajay.seenu.expensetracker.android.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.TransactionDetail
import com.ajay.seenu.expensetracker.android.data.TransactionRepository
import com.ajay.seenu.expensetracker.android.domain.GetRecentTransactions
import com.ajay.seenu.expensetracker.entity.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.core.KoinApplication.Companion.init
import org.koin.core.parameter.emptyParametersHolder
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class OverviewScreenViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _spendSoFar: MutableStateFlow<String> = MutableStateFlow("Rs. 1000")
    val spendSoFar = _spendSoFar.asStateFlow()

    private val _recentTransactions: MutableStateFlow<List<Transaction>> =
        MutableStateFlow(listOf())
    val recentTransactions = _recentTransactions.asStateFlow()

    private val _userName: MutableStateFlow<String> = MutableStateFlow("")
    val userName = _userName.asStateFlow()

    @Inject
    internal lateinit var getRecentTransactions: GetRecentTransactions

    init {
        viewModelScope.launch {
            _userName.emit("Seenivasan T")
        }
    }

    fun getRecentTransactions() {
        viewModelScope.launch {
            getRecentTransactions.invoke().collectLatest {
                _recentTransactions.emit(it.map { it.transform() })
            }
        }
    }

}

// FIXME: Will be removed
fun TransactionDetail.transform(): Transaction {
    return Transaction(
        this.type.transform(),
        "",
        amount.toDouble(),
        category.name,
        Date()
    )
}

fun TransactionType.transform(): Transaction.Type {
    return when (this) {
        TransactionType.INCOME -> Transaction.Type.INCOME
        TransactionType.EXPENSE -> Transaction.Type.EXPENSE
    }
}