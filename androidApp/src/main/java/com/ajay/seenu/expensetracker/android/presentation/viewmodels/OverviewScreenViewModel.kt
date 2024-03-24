package com.ajay.seenu.expensetracker.android.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.TransactionDetail
import com.ajay.seenu.expensetracker.android.domain.usecases.GetOverallDataUseCase
import com.ajay.seenu.expensetracker.android.domain.usecases.GetRecentTransactionsUseCase
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
class OverviewScreenViewModel @Inject constructor() : ViewModel() {

    private val _overallData: MutableStateFlow<OverallData?> = MutableStateFlow(null)
    val overallData = _overallData.asStateFlow()

    private val _recentTransactions: MutableStateFlow<HashMap<String, out List<Transaction>>> =
        MutableStateFlow(hashMapOf())
    val recentTransactions = _recentTransactions.asStateFlow()

    private val _userName: MutableStateFlow<String> = MutableStateFlow("")
    val userName = _userName.asStateFlow()

    @Inject
    internal lateinit var getRecentTransactions: GetRecentTransactionsUseCase

    @Inject
    internal lateinit var getOverallDataUseCase: GetOverallDataUseCase

    init {
        viewModelScope.launch {
            _userName.emit("Seenivasan T")
        }
    }

    fun getOverallData() {
        viewModelScope.launch {
            getOverallDataUseCase().collectLatest {
                _overallData.emit(it)
            }
        }
    }

    fun getRecentTransactions() {
        viewModelScope.launch {
            val dateFormatter = SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH)
            getRecentTransactions.invoke().collectLatest {
                val map = HashMap<String, ArrayList<Transaction>>()
                it.forEach {
                    val transaction = it.transform()
                    val date = dateFormatter.format(Date(it.date))
                    map[date]?.let {
                        it.add(transaction)
                    } ?: run {
                        map[date] = arrayListOf(transaction)
                    }
                }
                _recentTransactions.emit(map)
            }
        }
    }

}

// FIXME: Will be removed
fun TransactionDetail.transform(): Transaction {
    return Transaction(
        category.name,
        this.type.transform(),
        this.note ?: "",
        amount.toDouble(),
        paymentType.name,
        Date(this.date)
    )
}

fun TransactionType.transform(): Transaction.Type {
    return when (this) {
        TransactionType.INCOME -> Transaction.Type.INCOME
        TransactionType.EXPENSE -> Transaction.Type.EXPENSE
    }
}