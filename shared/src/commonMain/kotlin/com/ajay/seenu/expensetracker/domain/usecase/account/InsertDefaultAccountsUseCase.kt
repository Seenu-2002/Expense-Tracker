package com.ajay.seenu.expensetracker.domain.usecase.account

import com.ajay.seenu.expensetracker.data.repository.AccountRepository
import com.ajay.seenu.expensetracker.domain.model.Account
import com.ajay.seenu.expensetracker.domain.model.AccountType

class InsertDefaultAccountsUseCase constructor(
    private val repository: AccountRepository
) {

    suspend operator fun invoke() {
        val accounts = getDefaultAccounts()
        for (account in accounts) {
            repository.createAccount(account)
        }
    }

    private fun getDefaultAccounts(): List<Account> = listOf(
        Account(
            id = 0L,
            name = "Cash",
            type = AccountType.CASH,
            isDefault = true
        ),
        Account(
            id = 0L,
            name = "Credit Card",
            type = AccountType.CREDIT_CARD,
            isDefault = true
        ),
        Account(
            id = 0L,
            name = "Bank Account",
            type = AccountType.BANK_ACCOUNT,
            isDefault = true
        )
    )

}