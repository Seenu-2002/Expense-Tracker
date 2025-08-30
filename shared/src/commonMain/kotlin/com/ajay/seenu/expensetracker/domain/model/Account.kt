package com.ajay.seenu.expensetracker.domain.model

data class Account constructor(
    val id: Long,
    val name: String,
    val type: AccountType,
    val isDefault: Boolean = false,
)

enum class AccountType {
    CASH, BANK_ACCOUNT, CREDIT_CARD, LOAN, INVESTMENT, OTHERS;
}