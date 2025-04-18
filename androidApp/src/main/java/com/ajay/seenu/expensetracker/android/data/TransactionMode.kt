package com.ajay.seenu.expensetracker.android.data

sealed class TransactionMode {
    data object New: TransactionMode()
    data class Edit(val id: Long): TransactionMode()
    data class Clone(val id: Long): TransactionMode()
}