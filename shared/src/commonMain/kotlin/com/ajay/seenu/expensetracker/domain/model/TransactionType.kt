package com.ajay.seenu.expensetracker.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class TransactionType {
    INCOME, EXPENSE
}