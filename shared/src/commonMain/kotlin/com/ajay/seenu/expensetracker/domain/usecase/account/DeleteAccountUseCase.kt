package com.ajay.seenu.expensetracker.domain.usecase.account

import com.ajay.seenu.expensetracker.data.repository.AccountRepository
import com.ajay.seenu.expensetracker.domain.model.Account

class DeleteAccountUseCase constructor(
    private val accountRepository: AccountRepository
) {

    suspend operator fun invoke(account: Account) {
        val account = accountRepository.getAccountById(account.id)
            ?: throw IllegalArgumentException("Account with id ${account.id} not found")
        accountRepository.deleteAccount(account)
    }

}