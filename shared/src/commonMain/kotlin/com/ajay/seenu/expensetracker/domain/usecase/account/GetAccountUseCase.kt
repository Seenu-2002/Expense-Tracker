package com.ajay.seenu.expensetracker.domain.usecase.account

import com.ajay.seenu.expensetracker.data.repository.AccountRepository

class GetAccountUseCase constructor(
    private val accountRepository: AccountRepository
) {

    suspend operator fun invoke(id: Long) = accountRepository.getAccountById(id)

}