package com.ajay.seenu.expensetracker.android.presentation.screeens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ajay.seenu.expensetracker.android.R
import com.ajay.seenu.expensetracker.android.presentation.components.AccountsListContent
import com.ajay.seenu.expensetracker.android.presentation.components.ChangeConfirmationDialog
import com.ajay.seenu.expensetracker.android.presentation.components.ProgressDialog
import com.ajay.seenu.expensetracker.android.presentation.state.UiState
import com.ajay.seenu.expensetracker.android.presentation.viewmodels.AccountsListViewModel
import com.ajay.seenu.expensetracker.domain.model.Account
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsListScreen(
    onCreateAccount: () -> Unit,
    onEditAccount: (id: Long) -> Unit,
    onDeleteAccount: (id: Long, transactionCount: Long) -> Unit,
    onNavigateBack: () -> Unit
) {
    val viewModel: AccountsListViewModel = hiltViewModel()

    val accounts by viewModel.accounts.collectAsStateWithLifecycle()
    val transactionCountInAccount by viewModel.transactionCountInAccount.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        if (accounts == UiState.Empty) {
            viewModel.getAccounts()
        }
    }
    val context = LocalContext.current

    var accountToBeDeleted: Account? by remember { mutableStateOf(null) }
    var showConfirmationDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(), topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ), title = {
                    Text(
                        modifier = Modifier.padding(horizontal = 4.dp),
                        text = stringResource(R.string.accounts),
                        fontWeight = FontWeight.SemiBold
                    )
                }, navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            modifier = Modifier
                                .clip(RoundedCornerShape(percent = 50))
                                .clickable(
                                    onClick = {
                                        onNavigateBack.invoke()
                                    }
                                )
                                .padding(8.dp),
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                })
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            AnimatedVisibility(
                visible = true,
                enter = scaleIn(animationSpec = tween(100)),
                exit = scaleOut(animationSpec = tween(100)),
            ) {
                FloatingActionButton(
                    onClick = onCreateAccount,
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.add_account)
                    )
                }
            }
        }) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {

            when (val accountsState = accounts) {
                UiState.Empty, UiState.Loading -> {
                    CircularProgressIndicator()
                }

                is UiState.Success -> {
                    AccountsListContent(
                        modifier = Modifier.matchParentSize(),
                        accounts = accountsState.data,
                        isClickable = false,
                        onAccountEdit = { it ->
                            onEditAccount(it.id)
                        },
                        onAccountDelete = {
                            accountToBeDeleted = it
                            val accounts = accountsState.data.accounts

                            if (accounts.size == 1) {
                                Toast
                                    .makeText(
                                        context,
                                        context.getString(R.string.error_last_account_to_delete),
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()
                                return@AccountsListContent
                            }

                            viewModel.getTransactionCountInAccount(accountToBeDeleted!!)
                        }
                    )
                }

                is UiState.Failure -> {
                    Text(
                        text = stringResource(R.string.something_went_wrong),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            when (val transactionCountState = transactionCountInAccount) {
                UiState.Empty -> {}
                UiState.Loading -> {
                    ProgressDialog()
                }

                is UiState.Success -> {
                    LaunchedEffect(Unit) {
                        if (transactionCountState.data == 0L) {
                            showConfirmationDialog = true
                        } else {
                            onDeleteAccount(accountToBeDeleted!!.id, transactionCountState.data)
                            viewModel.resetCount()
                        }
                    }
                }

                is UiState.Failure -> {
                    Timber.e("Unable to fetch transaction count by account :: ${transactionCountState.error}")
                    Text(
                        text = stringResource(R.string.something_went_wrong),
                        color = MaterialTheme.colorScheme.error
                    )
                    viewModel.resetCount()
                }
            }

            if (showConfirmationDialog) {
                val categoryToBeDeleted = accountToBeDeleted ?: return@Scaffold
                ChangeConfirmationDialog(
                    title = stringResource(R.string.delete_account_title),
                    message = stringResource(
                        R.string.delete_account_message,
                        categoryToBeDeleted.name,
                    ),
                    onDismiss = {
                        showConfirmationDialog = false
                    },
                    onConfirm = {
                        viewModel.deleteAccount(categoryToBeDeleted)
                        Toast.makeText(
                            context,
                            context.getString(
                                R.string.delete_account_success,
                                categoryToBeDeleted.name
                            ),
                            Toast.LENGTH_SHORT
                        ).show()
                        showConfirmationDialog = false
                    }
                )
            }

        }
    }

}