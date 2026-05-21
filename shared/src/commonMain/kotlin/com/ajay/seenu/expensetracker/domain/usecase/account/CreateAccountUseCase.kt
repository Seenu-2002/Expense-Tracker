package com.ajay.seenu.expensetracker.domain.usecase.account

import com.ajay.seenu.expensetracker.data.repository.AccountRepository
import com.ajay.seenu.expensetracker.domain.model.Account
import com.ajay.seenu.expensetracker.domain.model.AccountType

class CreateAccountUseCase constructor(
    private val accountRepository: AccountRepository
) {

    suspend operator fun invoke(name: String, type: AccountType, initialBalance: Double = 0.0) {
        val account = Account(0L, name, type, false, initialBalance)
        accountRepository.createAccount(account)
    }

}