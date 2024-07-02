package com.ajay.seenu.expensetracker.android.presentation.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor() : ViewModel() {

    val chartOrder: List<Charts> = Charts.entries

}

enum class Charts(val label: String) {
    TOTAL_EXPENSE_PER_DAY_BY_CATEGORY("Total Expense per day By Category"),
    EXPENSE_BY_CATEGORY("Expense by Category"),
    EXPENSE_BY_PAYMENT_TYPE("Expense by Payment Type")
}