package com.ajay.seenu.expensetracker.domain.usecase.account

import com.ajay.seenu.expensetracker.data.repository.AccountRepository
import com.ajay.seenu.expensetracker.domain.model.Account

class GetAccountsUseCase constructor(
    private val repository: AccountRepository
) {

    suspend operator fun invoke(): List<Account> {
        return repository.getAllAccounts()
    }

}