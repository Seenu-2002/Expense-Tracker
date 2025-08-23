package com.ajay.seenu.expensetracker.domain.model

import kotlinx.datetime.LocalDate

data class TransactionsByDate constructor(
    val rawDate: LocalDate,
    val transactions: List<Transaction>
)