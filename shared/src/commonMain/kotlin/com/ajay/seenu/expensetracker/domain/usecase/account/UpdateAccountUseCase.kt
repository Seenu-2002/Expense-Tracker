package com.ajay.seenu.expensetracker.domain.usecase.account

import com.ajay.seenu.expensetracker.data.repository.AccountRepository
import com.ajay.seenu.expensetracker.domain.model.AccountType

class UpdateAccountUseCase constructor(
    private val accountRepository: AccountRepository
) {

    suspend operator fun invoke(id: Long, name: String, type: AccountType) {
        val account = accountRepository.getAccountById(id)
            ?: throw IllegalArgumentException("Account with id $id not found")
        val updatedAccount =
            account.copy(name = name, type = type)
        accountRepository.updateAccount(updatedAccount)
    }

}