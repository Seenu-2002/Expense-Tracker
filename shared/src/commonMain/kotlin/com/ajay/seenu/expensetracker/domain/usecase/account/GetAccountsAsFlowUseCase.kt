package com.ajay.seenu.expensetracker.domain.usecase.account

import com.ajay.seenu.expensetracker.data.repository.AccountRepository
import com.ajay.seenu.expensetracker.domain.model.Account
import kotlinx.coroutines.flow.Flow

class GetAccountsAsFlowUseCase constructor(
    private val repository: AccountRepository
) {

    suspend operator fun invoke(): Flow<List<Account>> {
        return repository.getAllAccountsAsFlow()
    }

}