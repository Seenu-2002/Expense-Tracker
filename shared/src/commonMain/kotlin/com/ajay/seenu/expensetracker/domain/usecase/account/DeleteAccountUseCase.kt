package com.ajay.seenu.expensetracker.domain.usecase.account

import com.ajay.seenu.expensetracker.data.repository.AccountRepository

class DeleteAccountUseCase constructor(
    private val accountRepository: AccountRepository
) {

    suspend operator fun invoke(id: Long) {
        val account = accountRepository.getAccountById(id)
            ?: throw IllegalArgumentException("Account with id $id not found")
        accountRepository.deleteAccount(account)
    }

}