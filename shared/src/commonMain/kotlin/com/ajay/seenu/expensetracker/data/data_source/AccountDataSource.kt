package com.ajay.seenu.expensetracker.data.data_source

import com.ajay.seenu.expensetracker.AccountEntity
import com.ajay.seenu.expensetracker.data.model.AccountTypeEntity
import kotlinx.coroutines.flow.Flow

interface AccountDataSource {

    fun getAccountsByType(type: AccountTypeEntity): List<AccountEntity>

    fun getAllAccounts(): List<AccountEntity>
    fun getAllAccountsAsFlow(): Flow<List<AccountEntity>>

    fun getAccount(id: Long): AccountEntity?

    fun createAccount(
        name: String,
        type: AccountTypeEntity,
        isDefault: Boolean,
    )

    fun updateAccount(
        id: Long,
        name: String,
        type: AccountTypeEntity
    )

    fun deleteAccount(id: Long)

}