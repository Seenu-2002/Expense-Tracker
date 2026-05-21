package com.ajay.seenu.expensetracker.android.presentation.state

import com.ajay.seenu.expensetracker.domain.model.Account
import com.ajay.seenu.expensetracker.domain.model.AccountType

data class AccountsListUiModel constructor(
    val accounts: List<Account>,
    val balances: Map<Long, Double> = emptyMap(),
) {
    val typeVsAccountMap: Map<AccountType, List<Account>> = accounts.groupBy { it.type }
}