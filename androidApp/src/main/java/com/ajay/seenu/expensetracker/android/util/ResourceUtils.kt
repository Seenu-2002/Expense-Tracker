package com.ajay.seenu.expensetracker.android.util

import androidx.compose.ui.graphics.Color
import com.ajay.seenu.expensetracker.android.R
import com.ajay.seenu.expensetracker.android.presentation.common.ChartDefaults
import com.ajay.seenu.expensetracker.domain.model.TransactionType

fun TransactionType.getStringRes(): Int {
    return when (this) {
        TransactionType.INCOME -> R.string.income
        TransactionType.EXPENSE -> R.string.expense
    }
}

fun TransactionType.getPlaceHolderRes(): Int {
    return when (this) {
        TransactionType.INCOME -> R.string.income_format
        TransactionType.EXPENSE -> R.string.expense_format
    }
}

fun TransactionType.getColor(): Color {
    return when (this) {
        TransactionType.INCOME -> ChartDefaults.incomeColor
        TransactionType.EXPENSE -> ChartDefaults.expenseColor
    }
}

fun TransactionType.getIconRes(): Int {
    return when (this) {
        TransactionType.INCOME -> R.drawable.income
        TransactionType.EXPENSE -> R.drawable.expense
    }
}