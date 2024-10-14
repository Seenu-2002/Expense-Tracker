package com.ajay.seenu.expensetracker.android.presentation.widgets

import android.content.res.Configuration
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.ajay.seenu.expensetracker.Attachment
import com.ajay.seenu.expensetracker.android.R
import com.ajay.seenu.expensetracker.android.domain.data.Transaction
import com.ajay.seenu.expensetracker.android.domain.util.getFileInfoFromUri
import com.ajay.seenu.expensetracker.android.domain.util.getFileNameFromUri
import com.ajay.seenu.expensetracker.android.domain.util.saveBitmapToFile
import com.ajay.seenu.expensetracker.android.presentation.common.MultiSelectChipsView
import com.ajay.seenu.expensetracker.entity.PaymentType
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
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
    onCategoryClicked: (selectedValue: Transaction.Category?) -> Unit,
    onTransactionTypeChanged: (type: Transaction.Type) -> Unit,
    onPaymentTypeClicked: (type: PaymentType?) -> Unit,
    onAdd: (transaction: Transaction, attachments: List<Uri>) -> Unit,
) {
    val context = LocalContext.current

    var selectedImageUriList by remember {
        mutableStateOf<List<Uri>>(emptyList())
    }
    var imageFile by remember { mutableStateOf<Uri?>(null) }
    val attachments = remember {
        mutableStateListOf<Uri>()
    }
    LaunchedEffect(selectedImageUriList, imageFile) {
        selectedImageUriList.forEach {
            if(!attachments.contains(it))
                attachments.add(it)
        }
        imageFile?.let { attachments.add(it) }
    }
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uriList ->
        if(attachments.size >= 5)
            Toast.makeText(context, "Cannot add more than 5 attachments", Toast.LENGTH_SHORT).show()
        else {
            val availableSpace = 5 - attachments.size
            if (uriList.size > availableSpace) {
                Toast.makeText(context, "Cannot add more than 5 attachments", Toast.LENGTH_SHORT).show()
            } else {
                selectedImageUriList = uriList
                //viewModel.addAttachmentFromUri(transactionId, uri, context)
            }
        }
    }
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            if(attachments.size >= 5)
                Toast.makeText(context, "Cannot add more than 5 attachments", Toast.LENGTH_SHORT).show()
            else
                imageFile = saveBitmapToFile(context, bitmap)
            //viewModel.addAttachmentFromFile(transactionId, imageFile)
        }
    }
    var showAddAttachmentDialog by rememberSaveable {
        mutableStateOf(false)
    }

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

        MultiSelectChipsView(
            modifier = Modifier.fillMaxWidth()
                .padding(vertical = 10.dp),
            selectedOptions =  attachments,
            onClick = {
                showAddAttachmentDialog = true
            },
            selectionOptionView = {
                Text(text = getFileNameFromUri(context, it) ?: "N/A")
            },
            onOptionCanceled = {
                attachments.remove(it)
            }
        )

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

                onAdd(newTransaction, attachments)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Add")
        }
    }

    if(showAddAttachmentDialog) {
        Dialog(
            onDismissRequest = {
                showAddAttachmentDialog = false
            },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 15.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color = MaterialTheme.colorScheme.background)
                    .padding(vertical = 10.dp)
            ) {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .clickable {
                        pickImageLauncher.launch("image/*")
                        showAddAttachmentDialog = false
                    }) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = R.drawable.baseline_upload_24),
                        contentDescription = "image gallery",
                    )
                    Text(
                        text = "Open Image Gallery",
                        modifier = Modifier.padding(start = 15.dp),
                    )
                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .clickable {
                        takePictureLauncher.launch(null)
                        showAddAttachmentDialog = false
                    }) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = R.drawable.baseline_camera_24),
                        contentDescription = "image gallery",
                    )
                    Text(
                        text = "Open Camera",
                        modifier = Modifier.padding(start = 15.dp),
                    )
                }
            }
        }
    }
}