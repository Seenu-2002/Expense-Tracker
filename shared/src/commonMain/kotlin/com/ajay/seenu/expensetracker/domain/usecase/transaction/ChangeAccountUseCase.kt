package com.ajay.seenu.expensetracker.domain.usecase.transaction

import com.ajay.seenu.expensetracker.data.repository.TransactionRepository
import com.ajay.seenu.expensetracker.domain.model.Account

class ChangeAccountUseCase constructor(
    private val repository: TransactionRepository
){

    suspend operator fun invoke(
        oldAccount: Account,
        newAccount: Account
    ){
        return repository.replaceAccountInTransactions(
            oldAccountId = oldAccount.id,
            newAccountId = newAccount.id
        )
    }

}