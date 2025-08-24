package com.ajay.seenu.expensetracker.data.repository

import com.ajay.seenu.expensetracker.data.data_source.AccountDataSource
import com.ajay.seenu.expensetracker.data.mapper.toDomain
import com.ajay.seenu.expensetracker.data.mapper.toEntity
import com.ajay.seenu.expensetracker.domain.model.Account
import com.ajay.seenu.expensetracker.domain.model.AccountGroup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlin.math.absoluteValue

class AccountRepository constructor(
    private val accountDataSource: AccountDataSource,
) {
    suspend fun getAccountGroups(): List<AccountGroup> {
        return withContext(Dispatchers.IO) {
            val accounts = getAllAccounts()
            val map = hashMapOf<Long, MutableList<Account>>()

            for (account in accounts) {
                val groupId = account.groupId.absoluteValue
                if (map[groupId] == null) {
                    map[groupId] = mutableListOf()
                }

                map[groupId]!!.add(account)
            }


            accountDataSource.getAccountGroups().map {
                it.toDomain(
                    map[it.id]?.toList() ?: emptyList()
                )
            }
        }
    }

    suspend fun getAccountGroupById(id: Long): AccountGroup? {
        return withContext(Dispatchers.IO) {
            val accounts = getAccountsByGroupId(id)
            accountDataSource.getAccountGroup(id)?.toDomain(
                accounts = accounts
            )
        }
    }

    suspend fun addAccountGroup(accountGroup: AccountGroup) {
        return withContext(Dispatchers.IO) {
            accountDataSource.createAccountGroup(accountGroup.name, accountGroup.type.toEntity())
        }
    }

    suspend fun updateAccountGroup(accountGroup: AccountGroup) {
        return withContext(Dispatchers.IO) {
            accountDataSource.updateAccountGroup(
                accountGroup.id,
                accountGroup.name,
                accountGroup.type.toEntity(),
            )
        }
    }

    suspend fun deleteAccountGroup(group: AccountGroup) {
        return withContext(Dispatchers.IO) {
            accountDataSource.deleteAccountGroup(group.id)
        }
    }

    suspend fun getAccountsByGroupId(groupId: Long): List<Account> {
        return withContext(Dispatchers.IO) {
            accountDataSource.getAccountGroupAccounts(groupId).map { it.toDomain() }
        }
    }

    suspend fun getAllAccounts(): List<Account> {
        return withContext(Dispatchers.IO) {
            accountDataSource.getAllAccounts().map { it.toDomain() }
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
                account.groupId,
                account.name,
            )
        }
    }

    suspend fun updateAccount(account: Account) {
        return withContext(Dispatchers.IO) {
            accountDataSource.updateAccount(
                account.id,
                account.groupId,
                account.name,
            )
        }
    }

    suspend fun deleteAccount(account: Account) {
        return withContext(Dispatchers.IO) {
            accountDataSource.deleteAccount(account.id)
        }
    }

}