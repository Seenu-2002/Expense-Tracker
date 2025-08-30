package com.ajay.seenu.expensetracker.android.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ajay.seenu.expensetracker.android.presentation.common.SwipeableBox
import com.ajay.seenu.expensetracker.android.presentation.state.AccountsListUiModel
import com.ajay.seenu.expensetracker.android.presentation.theme.ExpenseTrackerTheme
import com.ajay.seenu.expensetracker.android.util.getStringRes
import com.ajay.seenu.expensetracker.domain.model.Account
import com.ajay.seenu.expensetracker.domain.model.AccountType

@Preview
@Composable
private fun AccountsListPreview() {
    val accounts = listOf(
        Account(
            id = 1,
            name = "SBI",
            type = AccountType.BANK_ACCOUNT,
        ),
        Account(
            id = 2,
            name = "HDFC",
            type = AccountType.BANK_ACCOUNT,
        ),
        Account(
            id = 3,
            name = "ICICI",
            type = AccountType.BANK_ACCOUNT
        ),
        Account(
            id = 4,
            name = "Cash",
            type = AccountType.CASH
        ),
        Account(
            id = 5,
            name = "Amex",
            type = AccountType.CREDIT_CARD
        )
    )

    ExpenseTrackerTheme {
        AccountsListContent(
            accounts = AccountsListUiModel(accounts = accounts),
        )
    }
}

@Composable
fun AccountsListContent(
    modifier: Modifier = Modifier,
    accounts: AccountsListUiModel,
    isClickable: Boolean = true,
    onAccountEdit: (Account) -> Unit = {},
    onAccountDelete: (Account) -> Unit = {},
    onAccountClicked: (Account) -> Unit = {}
) {
    Column(modifier = modifier) {
        for ((type, accountsForType) in accounts.typeVsAccountMap) {
            AccountsGroupRow(
                modifier = Modifier,
                type = type,
                isClickable = isClickable,
                accounts = accountsForType,
                onAccountClicked = onAccountClicked,
                onAccountEdit = onAccountEdit,
                onAccountDelete = onAccountDelete
            )
        }
    }

}

@Preview
@Composable
private fun AccountsGroupRowPreview() {
    val accounts = listOf(
        Account(
            id = 1,
            name = "SBI",
            type = AccountType.BANK_ACCOUNT,
        ),
        Account(
            id = 2,
            name = "HDFC",
            type = AccountType.BANK_ACCOUNT,
        ),
        Account(
            id = 3,
            name = "ICICI",
            type = AccountType.BANK_ACCOUNT
        )
    )
    ExpenseTrackerTheme {
        AccountsGroupRow(type = AccountType.BANK_ACCOUNT, accounts = accounts)
    }
}

@Composable
fun AccountsGroupRow(
    modifier: Modifier = Modifier,
    type: AccountType,
    accounts: List<Account>,
    isClickable: Boolean = true,
    selectedAccount: Account? = null,
    onAccountEdit: (Account) -> Unit = {},
    onAccountDelete: (Account) -> Unit = {},
    onAccountClicked: (Account) -> Unit = {}
) {
    if (accounts.isEmpty()) {
        return
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // TODO: Proper string resource
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .padding(vertical = 8.dp, horizontal = 12.dp),
            text = stringResource(type.getStringRes()),
            fontWeight = FontWeight.Medium
        )
        for (account in accounts) {
            SwipeableBox(
                modifier = Modifier.fillMaxWidth(),
                onDelete = {
                    onAccountDelete(account)
                },
                onEdit = {
                    onAccountEdit(account)
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colorScheme.surface)
                        .padding(vertical = 8.dp, horizontal = 12.dp)
                        .clickable(enabled = isClickable) { onAccountClicked(account) },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = account.name
                    )
                    if (account == selectedAccount) {
                        Icon(imageVector = Icons.Rounded.Check, contentDescription = null)
                    }
                }
            }
        }
    }

}