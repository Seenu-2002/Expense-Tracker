package com.ajay.seenu.expensetracker.data.repository

import com.ajay.seenu.expensetracker.data.data_source.AccountDataSource
import com.ajay.seenu.expensetracker.data.mapper.toDomain
import com.ajay.seenu.expensetracker.data.mapper.toEntity
import com.ajay.seenu.expensetracker.domain.model.Account
import com.ajay.seenu.expensetracker.domain.model.AccountType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AccountRepository constructor(
    private val accountDataSource: AccountDataSource,
) {

    suspend fun getAccountsByType(type: AccountType): List<Account> {
        return withContext(Dispatchers.IO) {
            accountDataSource.getAccountsByType(type.toEntity()).map { it.toDomain() }
        }
    }

    suspend fun getAllAccounts(): List<Account> {
        return withContext(Dispatchers.IO) {
            accountDataSource.getAllAccounts().map { it.toDomain() }
        }
    }

    suspend fun getAllAccountsAsFlow(): Flow<List<Account>> {
        return withContext(Dispatchers.IO) {
            accountDataSource.getAllAccountsAsFlow().map {
                it.map { entity -> entity.toDomain() }
            }
        }
    }

    suspend fun getAccountById(id: Long): Account? {
        return withContext(Dispatchers.IO) {
            accountDataSource.getAccount(id)?.toDomain()
        }
    }

    suspend fun createAccount(account: Account) {
        return withContext(Dispatchers.IO) {
            accountDataSource.createAccount(
                account.name,
                account.type.toEntity(),
                account.isDefault
            )
        }
    }

    suspend fun updateAccount(account: Account) {
        return withContext(Dispatchers.IO) {
            accountDataSource.updateAccount(
                account.id,
                account.name,
                account.type.toEntity(),
            )
        }
    }

    suspend fun deleteAccount(account: Account) {
        return withContext(Dispatchers.IO) {
            accountDataSource.deleteAccount(account.id)
        }
    }

}