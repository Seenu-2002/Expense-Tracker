package com.ajay.seenu.expensetracker.android.presentation.screeens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ajay.seenu.expensetracker.android.R
import com.ajay.seenu.expensetracker.android.presentation.common.TransactionFieldView
import com.ajay.seenu.expensetracker.android.presentation.components.DiscardChangesDialog
import com.ajay.seenu.expensetracker.android.presentation.components.ProgressDialog
import com.ajay.seenu.expensetracker.android.presentation.state.UiState
import com.ajay.seenu.expensetracker.android.presentation.viewmodels.AddEditAccountViewModel
import com.ajay.seenu.expensetracker.android.util.getStringRes
import com.ajay.seenu.expensetracker.domain.model.AccountType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAccountScreen(arg: AddEditAccountScreenArg, onNavigateBack: () -> Unit) {

    val viewModel: AddEditAccountViewModel = hiltViewModel()
    val isInEditMode = arg is AddEditAccountScreenArg.Edit

    val status by viewModel.status.collectAsStateWithLifecycle()
    val accountState by viewModel.account.collectAsStateWithLifecycle()
    val account = (accountState as? UiState.Success?)?.data


    LaunchedEffect(Unit) {
        if (isInEditMode && accountState == UiState.Empty) {
            viewModel.getAccount(arg.id)
        }
    }

    var label by remember(account) { mutableStateOf(account?.name ?: "") }
    var type by remember(account) { mutableStateOf(account?.type ?: AccountType.CASH) }
    val context = LocalContext.current

    var showTypeBottomSheet by remember { mutableStateOf(false) }
    val accountTypeSheetState = rememberModalBottomSheetState()
    var showLabelError by remember { mutableStateOf(false) }

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ), title = {
                    val titleRes = if (isInEditMode) {
                        R.string.edit_account
                    } else {
                        R.string.add_account
                    }
                    Text(
                        modifier = Modifier.padding(horizontal = 4.dp),
                        text = stringResource(titleRes),
                        fontWeight = FontWeight.SemiBold
                    )
                }, navigationIcon = {
                    // TODO: Backpress dispatcher
                    IconButton(onClick = {

                        if (isInEditMode) {
                            if (label != account?.name || type != account.type) {
                                showExitDialog = true
                            } else {
                                onNavigateBack()
                            }
                        } else {
                            if (label.isNotBlank()) {
                                showExitDialog = true
                            } else {
                                onNavigateBack()
                            }
                        }

                    }) {
                        Icon(
                            modifier = Modifier
                                .clip(RoundedCornerShape(percent = 50))
                                .padding(8.dp),
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                })
        },
        floatingActionButton = {
            if (isInEditMode && account == null) {
                return@Scaffold
            }

            ExtendedFloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                onClick = {
                    if (label.isNotBlank()) {
                        if (arg is AddEditAccountScreenArg.Edit) {
                            val account = account!!
                            val hasChanges =
                                account.name != label || account.type != type

                            if (!hasChanges) {
                                Toast.makeText(
                                    context,
                                    R.string.no_changes_made,
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@ExtendedFloatingActionButton
                            }

                            viewModel.updateAccount(
                                id = account.id,
                                name = label,
                                type = type
                            )
                        } else {
                            viewModel.createAccount(
                                name = label,
                                type = type
                            )
                        }
                    } else {
                        showLabelError = true
                        Toast.makeText(
                            context,
                            R.string.error_empty_category_title,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                },
                shape = CircleShape,
                text = {
                    val text = if (isInEditMode) {
                        stringResource(R.string.update)
                    } else {
                        stringResource(R.string.create)
                    }
                    Text(text = text)
                }, icon = {
                    Icon(
                        painter = painterResource(R.drawable.check),
                        contentDescription = null,
                        tint = Color.White
                    )
                })
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            var showForm = false
            if (isInEditMode) {
                when (accountState) {
                    is UiState.Loading -> {
                        CircularProgressIndicator()
                    }

                    is UiState.Failure -> {
                        Text(text = stringResource(R.string.something_went_wrong))
                    }

                    is UiState.Success -> {
                        showForm = true
                    }

                    else -> { /* Do nothing */
                    }
                }
            } else {
                showForm = true
            }

            if (showForm) {
                AccountForm(
                    modifier = Modifier.fillMaxSize(),
                    label = label,
                    type = type,
                    isLabelError = showLabelError,
                    onLabelChanged = {
                        label = it
                        showLabelError = false
                    },
                    onAccountClicked = {
                        showTypeBottomSheet = true
                    }
                )
            }

            when (status) {
                UiState.Empty -> {}
                UiState.Loading -> {
                    ProgressDialog()
                }

                is UiState.Failure -> {
                    Toast.makeText(
                        context,
                        R.string.something_went_wrong,
                        Toast.LENGTH_SHORT
                    ).show()
                    onNavigateBack()
                }

                is UiState.Success -> {
                    val toastMessageRes =
                        if (isInEditMode) R.string.account_updated else R.string.account_created
                    val toastMessage = stringResource(
                        toastMessageRes, label
                    )

                    LaunchedEffect(Unit) {
                        Toast.makeText(
                            context,
                            toastMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                        onNavigateBack()
                    }
                }
            }
        }
    }

    if (showTypeBottomSheet) {
        AccountsTypeBottomSheet(
            sheetState = accountTypeSheetState,
            selectedType = type,
            onTypeSelected = {
                type = it
                showTypeBottomSheet = false
            },
            onDismiss = {
                showTypeBottomSheet = false
            }
        )
    }

    if (showExitDialog) {
        DiscardChangesDialog(onDiscardConfirmed = {
            showExitDialog = false
            onNavigateBack()
        }, onDismiss = {
            showExitDialog = false
        })
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsTypeBottomSheet(
    sheetState: SheetState,
    selectedType: AccountType?,
    onTypeSelected: (AccountType) -> Unit,
    onDismiss: () -> Unit,
) {
    val types = AccountType.entries

    ModalBottomSheet(sheetState = sheetState, onDismissRequest = onDismiss) {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(types) { type ->
                val label = stringResource(type.getStringRes())
                val isSelected = type == selectedType
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onTypeSelected(type)
                        },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = label,
                        modifier = Modifier
                            .weight(1F)
                            .padding(16.dp),
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            LocalContentColor.current
                        }
                    )

                    if (isSelected) {
                        Icon(
                            painter = painterResource(R.drawable.check),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .padding(end = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AccountForm(
    modifier: Modifier = Modifier,
    label: String = "",
    type: AccountType = AccountType.CASH,
    isLabelError: Boolean = false,
    onLabelChanged: (String) -> Unit = {},
    onAccountClicked: () -> Unit = {},
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            value = label,
            onValueChange = onLabelChanged,
            isError = isLabelError,
            placeholder = {
                Text(stringResource(R.string.account_name))
            },
            keyboardOptions = KeyboardOptions(
                KeyboardCapitalization.Words
            )
        )
        TransactionFieldView(
            modifier = Modifier
                .padding(8.dp),
            text = stringResource(type.getStringRes()),
            color = LocalContentColor.current,
            onClick = onAccountClicked
        )
    }
}

sealed interface AddEditAccountScreenArg {
    data object Create : AddEditAccountScreenArg
    data class Edit constructor(val id: Long) : AddEditAccountScreenArg
}