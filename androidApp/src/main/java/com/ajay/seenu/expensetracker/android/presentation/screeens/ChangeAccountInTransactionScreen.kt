package com.ajay.seenu.expensetracker.android.presentation.screeens

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ajay.seenu.expensetracker.android.R
import com.ajay.seenu.expensetracker.android.presentation.components.AccountsListContent
import com.ajay.seenu.expensetracker.android.presentation.components.ChangeConfirmationDialog
import com.ajay.seenu.expensetracker.android.presentation.components.ProgressDialog
import com.ajay.seenu.expensetracker.android.presentation.state.AccountsListUiModel
import com.ajay.seenu.expensetracker.android.presentation.state.Error
import com.ajay.seenu.expensetracker.android.presentation.state.UiState
import com.ajay.seenu.expensetracker.android.presentation.viewmodels.ChangeAccountInTransactionsViewModel
import com.ajay.seenu.expensetracker.domain.model.Account

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeAccountInTransactionScreen(
    accountToBeDeletedId: Long,
    transactionCount: Long,
    onNavigateBack: () -> Unit
) {
    val viewmodel: ChangeAccountInTransactionsViewModel = hiltViewModel()
    val accountsState by viewmodel.accounts.collectAsStateWithLifecycle()
    val updateStatus by viewmodel.updateStatus.collectAsStateWithLifecycle()
    val context = LocalContext.current

    lateinit var accountToBeReplacedWith: Account
    var showConfirmationDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (accountsState == UiState.Empty) {
            viewmodel.init(
                accountToBeDeletedId = accountToBeDeletedId
            )
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.change_account),
                    fontWeight = FontWeight.SemiBold
                )
            },
            navigationIcon = {
                IconButton(
                    onNavigateBack
                ) {
                    Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )
    }) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when (val state = accountsState) {
                UiState.Loading, UiState.Empty -> {
                    CircularProgressIndicator()
                }

                is UiState.Success -> {
                    val accountToBeDeleted = viewmodel.accountToBeDeleted
                    ChangeAccountInTransactionContent(
                        modifier = Modifier.matchParentSize(),
                        recordCount = transactionCount,
                        accountToBeDeleted = accountToBeDeleted,
                        accounts = state.data,
                    ) { account ->
                        accountToBeReplacedWith = account
                        showConfirmationDialog = true
                    }
                }

                is UiState.Failure -> {
                    if (state.error is Error.CategoryNotFound) {
                        Toast.makeText(
                            context,
                            R.string.error_account_not_found,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            context,
                            "Unable to change account :: ${state.error}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    onNavigateBack()
                }
            }

            when (val updateStatus = updateStatus) {
                UiState.Loading -> {
                    ProgressDialog()
                }

                is UiState.Success -> {
                    if (updateStatus.data) {
                        val msg = stringResource(
                            R.string.delete_account_success,
                            viewmodel.accountToBeDeleted.name
                        )
                        LaunchedEffect(updateStatus) {
                            Toast.makeText(
                                context,
                                msg,
                                Toast.LENGTH_SHORT
                            ).show()
                            onNavigateBack()
                        }
                    }
                }

                is UiState.Failure -> {
                    Toast.makeText(
                        context,
                        "Unable to update account :: ${updateStatus.error}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {}
            }

            if (showConfirmationDialog) {
                ChangeConfirmationDialog(
                    title = stringResource(R.string.replace_account_title),
                    message = stringResource(
                        R.string.replace_account_message,
                        viewmodel.accountToBeDeleted.name,
                        accountToBeReplacedWith.name
                    ),
                    confirmButtonText = stringResource(R.string.action_replace_delete),
                    onDismiss = {
                        showConfirmationDialog = false
                    },
                    onConfirm = {
                        viewmodel.updateAccount(accountToBeReplacedWith)
                        showConfirmationDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun ChangeAccountInTransactionContent(
    modifier: Modifier = Modifier,
    recordCount: Long,
    accountToBeDeleted: Account,
    accounts: List<Account>,
    onAccountSelected: (Account) -> Unit,
) {
    val accountsUiModel = AccountsListUiModel(accounts)
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painter = painterResource(R.drawable.ic_info), contentDescription = "Info Icon")
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                modifier = Modifier
                    .weight(1F),

                text = stringResource(
                    R.string.replace_account_info,
                    accountToBeDeleted.name,
                    recordCount
                ),
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        AccountsListContent(
            modifier = Modifier.fillMaxWidth(),
            isClickable = true,
            gesturesEnabled = false,
            accounts = accountsUiModel,
            onAccountClicked = onAccountSelected
        )
    }
}