package com.ajay.seenu.expensetracker.android.presentation.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.ajay.seenu.expensetracker.android.presentation.viewmodels.Transaction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun AddTransactionForm(
    modifier: Modifier = Modifier,
    transaction: Transaction? = null,
    formatter: SimpleDateFormat = SimpleDateFormat(
        "dd MMM, yyyy",
        Locale.ENGLISH
    ),
    onAdd: (transaction: Transaction) -> Unit = {}
) {
    var transactionType by remember {
        mutableStateOf(transaction?.type ?: Transaction.Type.INCOME)
    }
    var type by remember {
        mutableStateOf("")
    }
    var description by remember {
        mutableStateOf(transaction?.description ?: "")
    }
    var category by remember {
        mutableStateOf(transaction?.category ?: "Category")
    }
    var date by remember {
        mutableStateOf(transaction?.date ?: Date())
    }
    var amount: String by remember {
        mutableStateOf(transaction?.amount?.toString() ?: "0")
    }
    var showDialog by remember {
        mutableStateOf(false)
    }

    var datePickerState by remember {
        // FIXME: Year is hardcoded
        mutableStateOf(DatePickerState(Locale.ENGLISH, null, null,2020..2024, DisplayMode.Picker))
    }
    val focusRequester = remember {
        FocusRequester()
    }
    val focusManager = LocalFocusManager.current
    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = {
                focusManager.clearFocus()
                showDialog = false
            },
            confirmButton = {
                Text(
                    modifier = Modifier
                        .clickable {
                            focusManager.clearFocus()
                            showDialog = false
                            val millis = datePickerState.selectedDateMillis!!
                            date = Date(millis)
                        }
                        .padding(vertical = 6.dp, horizontal = 8.dp),
                    text = "Ok",
                    color = Color.Blue
                )
            }, dismissButton = {
                Text(
                    modifier = Modifier
                        .clickable {
                            focusManager.clearFocus()
                            showDialog = false
                        }
                        .padding(vertical = 6.dp, horizontal = 8.dp),
                    text = "Cancel",
                    color = Color.Red
                )
            }) {
            DatePicker(state = datePickerState)
        }
    }

    Column(modifier = modifier) {
        TransactionTypeSelector(transactionType = transactionType, onClick = {
            transactionType = it
            focusManager.clearFocus()
            category = ""
        })
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = type,
            label = {
                Text(text = "Type")
            },
            textStyle = LocalTextStyle.current, onValueChange = {
                type = it
            })
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = description,
            maxLines = 4,
            minLines = 3,
            label = {
                Text(text = "Description")
            },
            textStyle = LocalTextStyle.current, onValueChange = {
                description = it
                category = it
            })
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = category,
            readOnly = true,
            label = {
                Text(text = "Category")
            },
            textStyle = LocalTextStyle.current, onValueChange = {
                category = it
            })
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = amount.toString(),
            label = {
                Text(text = "Amount")
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            textStyle = LocalTextStyle.current,
            onValueChange = {
                amount = it
            })
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged {
                    if (it.isFocused) {
                        showDialog = true
                    }
                },
            value = formatter.format(date),
            readOnly = true,
            label = {
                Text(text = "Date")
            },
            textStyle = LocalTextStyle.current, onValueChange = { /* do nothing */ })
        Button(
            onClick = {
                val newTransaction = Transaction(
                    transactionType,
                    description,
                    amount.toDoubleOrNull() ?: 0.0,
                    category,
                    date
                )
                onAdd(newTransaction)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Add")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionTypeSelector(
    modifier: Modifier = Modifier,
    transactionType: Transaction.Type = Transaction.Type.EXPENSE,
    onClick: (Transaction.Type) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        val (expenseBackground, incomeBackground) = if (transactionType == Transaction.Type.INCOME) {
            Color.Transparent to Color.Green
        } else {
            Color.Green to Color.Transparent
        }

        Text(
            text = "Income",
            modifier = Modifier
                .fillMaxWidth(.5F)
                .padding(end = 4.dp)
                .background(color = incomeBackground)
                .clickable { onClick.invoke(Transaction.Type.INCOME) },
            textAlign = TextAlign.Center
        )
        Text(
            text = "Expense",
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp)
                .background(color = expenseBackground)
                .clickable { onClick.invoke(Transaction.Type.EXPENSE) },
            textAlign = TextAlign.Center
        )
    }
}