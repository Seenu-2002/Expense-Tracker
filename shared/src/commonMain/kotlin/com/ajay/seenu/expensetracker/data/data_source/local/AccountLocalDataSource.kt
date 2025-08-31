package com.ajay.seenu.expensetracker.data.data_source.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.ajay.seenu.expensetracker.AccountEntity
import com.ajay.seenu.expensetracker.ExpenseDatabase
import com.ajay.seenu.expensetracker.data.data_source.AccountDataSource
import com.ajay.seenu.expensetracker.data.model.AccountTypeEntity
import kotlinx.coroutines.Dispatchers

class AccountLocalDataSource constructor(
    database: ExpenseDatabase
) : AccountDataSource {

    private val queries = database.expenseDatabaseQueries

    override fun getAccountsByType(type: AccountTypeEntity): List<AccountEntity> {
        return queries.getAccountsByType(type = type).executeAsList()
    }

    override fun getAllAccounts(): List<AccountEntity> {
        return queries.getAllAccounts().executeAsList()
    }

    override fun getAllAccountsAsFlow(): kotlinx.coroutines.flow.Flow<List<AccountEntity>> {
        return queries.getAllAccounts().asFlow().mapToList(
            Dispatchers.Main
        )
    }

    override fun getAccount(id: Long): AccountEntity? {
        return queries.getAccountById(id).executeAsOneOrNull()
    }

    override fun createAccount(name: String, type: AccountTypeEntity, isDefault: Boolean) {
        return queries.createAccount(
            name = name,
            type = type,
            isDefault = if (isDefault) 1 else 0
        )
    }

    override fun updateAccount(
        id: Long,
        name: String,
        type: AccountTypeEntity
    ) {
        return queries.updateAccount(
            id = id,
            name = name,
            type = type
        )
    }

    override fun deleteAccount(id: Long) {
        return queries.deleteAccount(id)
    }

}