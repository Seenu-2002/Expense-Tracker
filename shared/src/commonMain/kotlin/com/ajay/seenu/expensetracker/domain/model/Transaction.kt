package com.ajay.seenu.expensetracker.domain.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

// TODO: Introduce Ui model
@OptIn(ExperimentalTime::class)
data class Transaction constructor(
    val id: Long,
    val type: TransactionType,
    val amount: Double,
    val category: Category,
    val account: Account,
    val createdAt: Instant,
    val note: String? = null
)