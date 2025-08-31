package com.ajay.seenu.expensetracker.data.mapper

import com.ajay.seenu.expensetracker.TransactionDetailEntity
import com.ajay.seenu.expensetracker.domain.model.Account
import com.ajay.seenu.expensetracker.domain.model.Category
import com.ajay.seenu.expensetracker.domain.model.Transaction
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun TransactionDetailEntity.toDomain(
    category: Category,
    account: Account
): Transaction {
    require(category.id == this.categoryId) {
        "Category ID mismatch: ${category.id} != ${this.categoryId}"
    }

    require(account.id == this.accountId) {
        "Account ID mismatch: ${account.id} != ${this.accountId}"
    }

    return Transaction(
        id = this.id,
        type = this.type.toDomain(),
        amount = this.amount,
        category = category,
        account = account,
        createdAt = Instant.fromEpochMilliseconds(this.createdAt),
        note = this.note
    )
}

@OptIn(ExperimentalTime::class)
fun Transaction.toEntity(): TransactionDetailEntity {
    return TransactionDetailEntity(
        id = this.id,
        type = this.type.toEntity(),
        amount = this.amount,
        categoryId = this.category.id,
        accountId = this.account.id,
        createdAt = this.createdAt.toEpochMilliseconds(),
        note = this.note,
        place = null // TODO: Feature to be supported
    )
}