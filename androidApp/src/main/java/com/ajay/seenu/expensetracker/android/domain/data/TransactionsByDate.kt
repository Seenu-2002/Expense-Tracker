package com.ajay.seenu.expensetracker.android.domain.data

import java.util.Date

data class TransactionsByDate(
    val rawDate: Date,
    val dateLabel: String,
    val transactions: List<Transaction>
)