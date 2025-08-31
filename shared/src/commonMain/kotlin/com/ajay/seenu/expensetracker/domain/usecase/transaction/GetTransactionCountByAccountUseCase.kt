package com.ajay.seenu.expensetracker.domain.usecase.transaction

import com.ajay.seenu.expensetracker.data.repository.TransactionRepository
import com.ajay.seenu.expensetracker.domain.model.Account

class GetTransactionCountByAccountUseCase constructor(
    private val repository: TransactionRepository
) {

    suspend operator fun invoke(account: Account): Long {
        return repository.getTransactionCountByAccountId(account.id)
    }

}