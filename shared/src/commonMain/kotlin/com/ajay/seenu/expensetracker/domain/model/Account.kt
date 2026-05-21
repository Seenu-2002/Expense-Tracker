package com.ajay.seenu.expensetracker.domain.model

data class Account constructor(
    val id: Long,
    val name: String,
    val type: AccountType,
    val isDefault: Boolean = false,
    val initialBalance: Double = 0.0,
)

enum class AccountType {
    CASH, BANK_ACCOUNT, CREDIT_CARD, LOAN, INVESTMENT, OTHERS;
}