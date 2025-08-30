package com.ajay.seenu.expensetracker.data.mapper

import com.ajay.seenu.expensetracker.AccountEntity
import com.ajay.seenu.expensetracker.data.model.AccountTypeEntity
import com.ajay.seenu.expensetracker.domain.model.Account
import com.ajay.seenu.expensetracker.domain.model.AccountType

fun AccountEntity.toDomain(): Account {
    return Account(
        id = id,
        name = name,
        isDefault = isDefault == 1L,
        type = type.toDomain(),
    )
}

fun Account.toEntity(): AccountEntity {
    return AccountEntity(
        id = id,
        name = name,
        isDefault = if (isDefault) 1L else 0L,
        type = type.toEntity(),
    )
}

fun AccountType.toEntity(): AccountTypeEntity {
    return when (this) {
        AccountType.CASH -> AccountTypeEntity.CASH
        AccountType.BANK_ACCOUNT -> AccountTypeEntity.BANK_ACCOUNT
        AccountType.CREDIT_CARD -> AccountTypeEntity.CREDIT_CARD
        else -> TODO()
    }
}

fun AccountTypeEntity.toDomain(): AccountType {
    return when (this) {
        AccountTypeEntity.CASH -> AccountType.CASH
        AccountTypeEntity.BANK_ACCOUNT -> AccountType.BANK_ACCOUNT
        AccountTypeEntity.CREDIT_CARD -> AccountType.CREDIT_CARD
        else -> TODO()
    }
}