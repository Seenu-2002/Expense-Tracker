package com.ajay.seenu.expensetracker.data.data_source

import com.ajay.seenu.expensetracker.AccountEntity
import com.ajay.seenu.expensetracker.AccountGroupEntity
import com.ajay.seenu.expensetracker.data.model.AccountGroupTypeEntity

interface AccountDataSource {

    fun getAccountGroups(): List<AccountGroupEntity>

    fun getAccountGroup(id: Long): AccountGroupEntity?

    fun createAccountGroup(
        name: String,
        type: AccountGroupTypeEntity,
    )

    fun updateAccountGroup(
        id: Long,
        name: String,
        type: AccountGroupTypeEntity,
    )

    fun deleteAccountGroup(id: Long)

    fun getAccountGroupAccounts(groupId: Long): List<AccountEntity>

    fun getAllAccounts(): List<AccountEntity>

    fun getAccount(id: Long): AccountEntity?

    fun createAccount(
        groupId: Long,
        name: String,
    )

    fun updateAccount(
        id: Long,
        groupId: Long,
        name: String,
    )

    fun deleteAccount(id: Long)

}