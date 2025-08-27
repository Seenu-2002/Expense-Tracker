package com.ajay.seenu.expensetracker.data.data_source.local

import com.ajay.seenu.expensetracker.AccountEntity
import com.ajay.seenu.expensetracker.AccountGroupEntity
import com.ajay.seenu.expensetracker.ExpenseDatabase
import com.ajay.seenu.expensetracker.data.data_source.AccountDataSource
import com.ajay.seenu.expensetracker.data.model.AccountGroupTypeEntity

class AccountLocalDataSource constructor(
    database: ExpenseDatabase
) : AccountDataSource {

    private val queries = database.expenseDatabaseQueries

    override fun getAccountGroups(): List<AccountGroupEntity> {
        return queries.getAccountGroups().executeAsList()
    }

    override fun getAccountGroup(id: Long): AccountGroupEntity? {
        return queries.getAccountGroupById(id).executeAsOneOrNull()
    }

    override fun createAccountGroup(name: String, type: AccountGroupTypeEntity) {
        return queries.createAccountGroup(
            name = name,
            type = type
        )
    }

    override fun updateAccountGroup(
        id: Long,
        name: String,
        type: AccountGroupTypeEntity
    ) {
        return queries.updateAccountGroup(
            id = id,
            name = name,
            type = type
        )
    }

    override fun deleteAccountGroup(id: Long) {
        return queries.deleteAccountGroup(id)
    }

    override fun getAccountGroupAccounts(groupId: Long): List<AccountEntity> {
        return queries.getAccountsByGroupId(groupId).executeAsList()
    }

    override fun getAllAccounts(): List<AccountEntity> {
        return queries.getAllAccounts().executeAsList()
    }

    override fun getAccount(id: Long): AccountEntity? {
        return queries.getAccountById(id).executeAsOneOrNull()
    }

    override fun createAccount(groupId: Long, name: String) {
        return queries.createAccount(
            groupId = groupId,
            name = name
        )
    }

    override fun updateAccount(
        id: Long,
        groupId: Long,
        name: String
    ) {
        return queries.updateAccount(
            id = id,
            groupId = groupId,
            name = name
        )
    }

    override fun deleteAccount(id: Long) {
        return queries.deleteAccount(id)
    }

}