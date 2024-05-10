package com.ajay.seenu.expensetracker.android.presentation.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ajay.seenu.expensetracker.android.R
import com.ajay.seenu.expensetracker.android.domain.data.Transaction
import com.ajay.seenu.expensetracker.entity.PaymentType
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
    selectedCategory: Transaction.Category? = transaction?.category,
    selectedPaymentType: PaymentType? = transaction?.paymentType,
    onCategoryClicked: (selectedValue: Transaction.Category?) -> Unit = {},
    onTransactionTypeChanged: (type: Transaction.Type) -> Unit = {},
    onPaymentTypeClicked: (type: PaymentType?) -> Unit = {},
    onAdd: (transaction: Transaction) -> Unit = {},
) {
    var transactionType by remember {
        mutableStateOf(transaction?.type ?: Transaction.Type.INCOME)
    }
    var description by remember {
        mutableStateOf(transaction?.note ?: "")
    }
    var date by remember {
        mutableStateOf(transaction?.date ?: Date())
    }
    var amount: String by remember {
        mutableStateOf(transaction?.amount?.toString() ?: "")
    }
    var showDialog by remember {
        mutableStateOf(false)
    }

    val datePickerState by remember {
        // FIXME: Year is hardcoded
        mutableStateOf(DatePickerState(Locale.ENGLISH, null, null, 2020..2024, DisplayMode.Picker))
    }
    val focusRequester = remember {
        FocusRequester()
    }
    val focusManager = LocalFocusManager.current

    val categoryInteractionSource = remember {
        MutableInteractionSource()
    }
    val paymentInteractionSource = remember {
        MutableInteractionSource()
    }

    var showCategoryError by remember {
        mutableStateOf(false)
    }

    var showPaymentTypeError by remember {
        mutableStateOf(false)
    }

    var showAmountError by remember {
        mutableStateOf(false)
    }


    if (categoryInteractionSource.collectIsPressedAsState().value) {
        showCategoryError = false
        onCategoryClicked.invoke(selectedCategory)
    }

    if (paymentInteractionSource.collectIsPressedAsState().value) {
        showPaymentTypeError = false
        onPaymentTypeClicked.invoke(selectedPaymentType)
    }

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
        SlidingSwitch(selectedValue = transactionType.value,
            values = Transaction.Type.entries.map { it.value },
            onSelectedValue = {
                val selectedTransactionType = Transaction.Type.valueOf(it.uppercase())
                onTransactionTypeChanged.invoke(selectedTransactionType)
                transactionType = selectedTransactionType
                focusManager.clearFocus()
            })
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = amount.toString(),
            label = {
                Text(text = stringResource(id = R.string.amount))
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            textStyle = LocalTextStyle.current,
            onValueChange = {
                amount = it
                showAmountError = it.isEmpty()
            },
            supportingText = {
                if (showAmountError) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        text = stringResource(R.string.enter_the_amount), // FIXME: String resource
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            isError = showAmountError)
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = selectedPaymentType?.label ?: "",
            label = {
                Text(text = stringResource(id = R.string.paymentType))
            },
            readOnly = true,
            interactionSource = paymentInteractionSource,
            textStyle = LocalTextStyle.current,
            onValueChange = {},
            supportingText = {
                if (showPaymentTypeError) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        text = "Select a valid Payment Type", // FIXME: String resource
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            isError = showPaymentTypeError)
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            interactionSource = categoryInteractionSource,
            value = selectedCategory?.label ?: "",
            readOnly = true,
            label = {
                Text(text = stringResource(id = R.string.category))
            },
            textStyle = LocalTextStyle.current,
            onValueChange = {},
            supportingText = {
                if (showCategoryError) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        text = "Select a valid Category", // FIXME: String resource
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            isError = showCategoryError)
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
            })
        val context = LocalContext.current
        Button(
            onClick = {
                if (selectedPaymentType == null) {
                    showPaymentTypeError = true

                }
                if (selectedCategory == null) {
                    showCategoryError = true
                }
                if(amount.toDoubleOrNull() == null) {
                    showAmountError = true
                }
                if(selectedPaymentType == null || selectedCategory == null || amount.toDoubleOrNull() == null)
                    return@Button

                val newTransaction = Transaction(
                    212L,
                    transactionType,
                    amount = amount.toDoubleOrNull() ?: 0.0,
                    category = selectedCategory,
                    paymentType = selectedPaymentType,
                    date = date,
                    note = description,
                )
                onAdd(newTransaction)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Add")
        }
    }
}