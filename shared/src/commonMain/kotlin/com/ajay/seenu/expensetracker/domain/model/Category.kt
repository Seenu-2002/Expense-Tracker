package com.ajay.seenu.expensetracker.domain.model

// TODO: Introduce Ui model
data class Category constructor(
    val id: Long,
    val type: TransactionType,
    val label: String,
    val color: Long,
    val iconRes: Int,
)