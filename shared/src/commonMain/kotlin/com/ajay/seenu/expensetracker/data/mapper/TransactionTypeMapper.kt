package com.ajay.seenu.expensetracker.data.mapper

import com.ajay.seenu.expensetracker.data.model.TransactionTypeEntity
import com.ajay.seenu.expensetracker.domain.model.TransactionType

fun TransactionTypeEntity.toDomain(): TransactionType {
    return when (this) {
        TransactionTypeEntity.INCOME -> TransactionType.INCOME
        TransactionTypeEntity.EXPENSE -> TransactionType.EXPENSE
        TransactionTypeEntity.TRANSFER -> TransactionType.TRANSFER
    }
}

fun TransactionType.toEntity(): TransactionTypeEntity {
    return when (this) {
        TransactionType.INCOME -> TransactionTypeEntity.INCOME
        TransactionType.EXPENSE -> TransactionTypeEntity.EXPENSE
        TransactionType.TRANSFER -> TransactionTypeEntity.TRANSFER
    }
}