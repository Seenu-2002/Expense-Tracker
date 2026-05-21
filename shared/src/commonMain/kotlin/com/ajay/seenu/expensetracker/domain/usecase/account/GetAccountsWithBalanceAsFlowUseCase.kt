package com.ajay.seenu.expensetracker.domain.usecase.account

import com.ajay.seenu.expensetracker.data.repository.AccountRepository
import com.ajay.seenu.expensetracker.domain.model.AccountWithBalance
import kotlinx.coroutines.flow.Flow

class GetAccountsWithBalanceAsFlowUseCase constructor(
    private val repository: AccountRepository
) {
    suspend operator fun invoke(): Flow<List<AccountWithBalance>> {
        return repository.getAllAccountsWithBalanceAsFlow()
    }
}
