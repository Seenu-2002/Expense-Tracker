package com.ajay.seenu.expensetracker.data.mapper

import com.ajay.seenu.expensetracker.GetAllTransactionsBetweenWithDetails
import com.ajay.seenu.expensetracker.GetAllTransactionsWithDetails
import com.ajay.seenu.expensetracker.GetDeletedTransactionsWithDetails
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
        note = this.note,
        place = this.place
    )
}

@OptIn(ExperimentalTime::class)
fun GetAllTransactionsWithDetails.toDomain(): Transaction {
    val category = Category(
        id = categoryId,
        type = categoryType.toDomain(),
        label = categoryLabel,
        color = categoryColor,
        iconRes = categoryIconRes.toInt()
    )
    val account = Account(
        id = accountId,
        name = accountName,
        isDefault = accountIsDefault == 1L,
        type = accountType.toDomain()
    )
    return Transaction(
        id = id,
        type = type.toDomain(),
        amount = amount,
        category = category,
        account = account,
        createdAt = Instant.fromEpochMilliseconds(createdAt),
        note = note,
        place = place
    )
}

@OptIn(ExperimentalTime::class)
fun GetAllTransactionsBetweenWithDetails.toDomain(): Transaction {
    val category = Category(
        id = categoryId,
        type = categoryType.toDomain(),
        label = categoryLabel,
        color = categoryColor,
        iconRes = categoryIconRes.toInt()
    )
    val account = Account(
        id = accountId,
        name = accountName,
        isDefault = accountIsDefault == 1L,
        type = accountType.toDomain()
    )
    return Transaction(
        id = id,
        type = type.toDomain(),
        amount = amount,
        category = category,
        account = account,
        createdAt = Instant.fromEpochMilliseconds(createdAt),
        note = note,
        place = place
    )
}

@OptIn(ExperimentalTime::class)
fun GetDeletedTransactionsWithDetails.toDomain(): Transaction {
    val category = Category(
        id = categoryId,
        type = categoryType.toDomain(),
        label = categoryLabel,
        color = categoryColor,
        iconRes = categoryIconRes.toInt()
    )
    val account = Account(
        id = accountId,
        name = accountName,
        isDefault = accountIsDefault == 1L,
        type = accountType.toDomain()
    )
    return Transaction(
        id = id,
        type = type.toDomain(),
        amount = amount,
        category = category,
        account = account,
        createdAt = Instant.fromEpochMilliseconds(createdAt),
        note = note,
        place = place
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
        place = this.place,
        deletedAt = null
    )
}