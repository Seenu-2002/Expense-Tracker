package com.ajay.seenu.expensetracker.data.mapper

import com.ajay.seenu.expensetracker.AccountEntity
import com.ajay.seenu.expensetracker.AccountGroupEntity
import com.ajay.seenu.expensetracker.data.model.AccountGroupTypeEntity
import com.ajay.seenu.expensetracker.domain.model.Account
import com.ajay.seenu.expensetracker.domain.model.AccountGroup
import com.ajay.seenu.expensetracker.domain.model.AccountType

fun AccountGroupEntity.toDomain(accounts: List<Account>): AccountGroup {
    require(accounts.all { it.groupId == id }) {
        "All accounts must belong to the group with id $id"
    }

    return AccountGroup(
        id = id,
        name = name,
        type = type.toDomain(),
        accounts = accounts,
    )
}

fun AccountGroup.toEntity(): AccountGroupEntity {
    return AccountGroupEntity(
        id = id,
        name = name,
        type = type.toEntity(),
    )
}

fun AccountEntity.toDomain(): Account {
    return Account(
        id = id,
        groupId = groupId,
        name = name,
    )
}

fun Account.toEntity(): AccountEntity {
    return AccountEntity(
        id = id,
        groupId = groupId,
        name = name,
    )
}

fun AccountType.toEntity(): AccountGroupTypeEntity {
    return when (this) {
        AccountType.CASH -> AccountGroupTypeEntity.CASH
        AccountType.BANK_ACCOUNT -> AccountGroupTypeEntity.BANK_ACCOUNT
        AccountType.CREDIT_CARD -> AccountGroupTypeEntity.CREDIT_CARD
    }
}

fun AccountGroupTypeEntity.toDomain(): AccountType {
    return when (this) {
        AccountGroupTypeEntity.CASH -> AccountType.CASH
        AccountGroupTypeEntity.BANK_ACCOUNT -> AccountType.BANK_ACCOUNT
        AccountGroupTypeEntity.CREDIT_CARD -> AccountType.CREDIT_CARD
    }
}