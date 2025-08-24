package com.ajay.seenu.expensetracker.domain.model

data class AccountGroup constructor(
    val id: Long,
    val name: String,
    val type: AccountType,
    val accounts: List<Account>
)

enum class AccountType {
    CASH, BANK_ACCOUNT, CREDIT_CARD
}