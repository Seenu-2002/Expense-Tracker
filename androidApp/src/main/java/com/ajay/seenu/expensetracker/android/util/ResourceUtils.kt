package com.ajay.seenu.expensetracker.android.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import com.ajay.seenu.expensetracker.android.R
import com.ajay.seenu.expensetracker.android.presentation.theme.AppTheme
import com.ajay.seenu.expensetracker.domain.model.AccountType
import com.ajay.seenu.expensetracker.domain.model.TransactionType

fun TransactionType.getStringRes(): Int {
    return when (this) {
        TransactionType.INCOME -> R.string.income
        TransactionType.EXPENSE -> R.string.expense
        TransactionType.TRANSFER -> R.string.transfer
    }
}

fun TransactionType.getPlaceHolderRes(): Int {
    return when (this) {
        TransactionType.INCOME -> R.string.income_format
        TransactionType.EXPENSE -> R.string.expense_format
        TransactionType.TRANSFER -> R.string.transfer_format
    }
}

@Composable
@ReadOnlyComposable
fun TransactionType.getColor(): Color {
    return when (this) {
        TransactionType.INCOME -> AppTheme.colors.income
        TransactionType.EXPENSE -> AppTheme.colors.expense
        TransactionType.TRANSFER -> AppTheme.colors.transfer
    }
}

fun TransactionType.getIconRes(): Int {
    return when (this) {
        TransactionType.INCOME -> R.drawable.income
        TransactionType.EXPENSE -> R.drawable.expense
        TransactionType.TRANSFER -> R.drawable.baseline_import_export_24
    }
}

fun AccountType.getStringRes(): Int {
    return when (this) {
        AccountType.CASH -> R.string.cash
        AccountType.BANK_ACCOUNT -> R.string.bank_accounts
        AccountType.CREDIT_CARD -> R.string.credit_cards
        AccountType.INVESTMENT -> R.string.investment
        AccountType.LOAN -> R.string.loan
        AccountType.OTHERS -> R.string.others
    }
}