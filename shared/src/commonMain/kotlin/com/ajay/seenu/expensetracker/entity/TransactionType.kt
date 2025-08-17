package com.ajay.seenu.expensetracker.entity

import kotlinx.serialization.Serializable

@Serializable
enum class TransactionType {
    INCOME, EXPENSE
}