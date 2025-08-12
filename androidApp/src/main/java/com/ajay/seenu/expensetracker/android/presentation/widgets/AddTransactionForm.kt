package com.ajay.seenu.expensetracker.android.presentation.widgets

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ajay.seenu.expensetracker.Attachment
import com.ajay.seenu.expensetracker.android.R
import com.ajay.seenu.expensetracker.android.domain.data.Transaction
import com.ajay.seenu.expensetracker.android.domain.util.getFileInfoFromUri
import com.ajay.seenu.expensetracker.android.domain.util.getFileNameFromUri
import com.ajay.seenu.expensetracker.android.domain.util.saveBitmapToFile
import com.ajay.seenu.expensetracker.android.presentation.common.MultiSelectChipsView
import com.ajay.seenu.expensetracker.android.presentation.common.PreviewThemeWrapper
import com.ajay.seenu.expensetracker.android.presentation.common.TransactionFieldView
import com.ajay.seenu.expensetracker.entity.PaymentType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionForm(
    modifier: Modifier = Modifier,
    transaction: Transaction? = null,
    existingAttachments: List<Attachment>? = null,  //TODO: must be shown in edit mode
    formatter: SimpleDateFormat = SimpleDateFormat(
        "dd MMM, yyyy",
        Locale.ENGLISH
    ),
    selectedCategory: Transaction.Category? = transaction?.category,
    selectedPaymentType: PaymentType? = transaction?.paymentType,
    onCategoryClicked: (selectedValue: Transaction.Category?) -> Unit,
    onTransactionTypeChanged: (type: Transaction.Type) -> Unit,
    onPaymentTypeClicked: (type: PaymentType?) -> Unit,
    onNavigateBack: () -> Unit,
    onAdd: (transaction: Transaction, attachments: List<Attachment>) -> Unit,
) {
    val context = LocalContext.current

    var selectedImageUriList by remember {
        mutableStateOf<List<Uri>>(emptyList())
    }
    var imageFile by remember { mutableStateOf<Uri?>(null) }
    val attachments = remember {
        mutableStateListOf<Attachment>().apply {
            existingAttachments?.let {
                addAll(it)
            }
        }
    }
    LaunchedEffect(selectedImageUriList, imageFile) {
        selectedImageUriList.forEach {
            val fileInfo = getFileInfoFromUri(context, it)
            val attachment = Attachment(
                id = 11L,
                transactionId = transaction?.id ?: 11L,
                name = fileInfo["fileName"] ?: "N/A",
                fileType = fileInfo["fileType"] ?: "N/A",
                filePath = fileInfo["filePath"] ?: "N/A",
                size = fileInfo["fileSize"]?.toLong() ?: 0L
            )
            if(!attachments.contains(attachment))
                attachments.add(attachment)
        }
        imageFile?.let {
            val fileInfo = getFileInfoFromUri(context, it)
            val attachment = Attachment(
                id = 11L,
                transactionId = transaction?.id ?: 11L,
                name = fileInfo["fileName"] ?: "N/A",
                fileType = fileInfo["fileType"] ?: "N/A",
                filePath = fileInfo["filePath"] ?: "N/A",
                size = fileInfo["fileSize"]?.toLong() ?: 0L
            )
            attachments.add(attachment)
        }
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

    var showCategoryError by remember {
        mutableStateOf(false)
    }

    var showPaymentTypeError by remember {
        mutableStateOf(false)
    }

    var showAmountError by remember {
        mutableStateOf(false)
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5F)
                .background(
                    color = if(transactionType == Transaction.Type.EXPENSE)
                        Color(0xFFFD3C4A)
                    else Color(0xFF00A86B),
                    shape = RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomStart = 25.dp,
                        bottomEnd = 25.dp
                    )
                )
                .align(Alignment.TopCenter)
        )
        Scaffold(
            topBar = {
                Box(
                    modifier = Modifier
                        //.fillMaxHeight(.1F)
                        .background(Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(.1F)
                            .background(Color.Transparent)
                    ) {
                        TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent
                        ), title = {
                            Text(
                                modifier = Modifier.padding(horizontal = 4.dp),
                                text = "Add Transaction",
                                color = Color.White
                            ) // TODO("string resource")
                        }, navigationIcon = {
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
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        })
                    }
                }
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Box(
                modifier = Modifier.fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 15.dp)
                    .fillMaxHeight(0.30F)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        SlidingSwitch(
                            modifier = Modifier.width(125.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(color = Color.White)
                                .padding(horizontal = 5.dp),
                            selectedValue = transactionType,
                            values = Transaction.Type.entries,
                            onSelectedValue = {
                                onTransactionTypeChanged.invoke(it)
                                focusManager.clearFocus()
                                transactionType = it
                            },
                            containerColor = Color.Transparent,
                            sliderColor = Color.LightGray,
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = stringResource(id = R.string.amount),
                        modifier = Modifier.padding(start = 20.dp),
                        color = Color.White)
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = amount,
                        prefix = {
                            Text(text = "$",
                                 style = LocalTextStyle.current.copy(
                                     color = Color.White,
                                    fontSize = 60.sp
                                )
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        textStyle = TextStyle(
                            fontSize = 60.sp,
                            color = Color.White
                        ),
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
                        singleLine = true,
                        isError = showAmountError,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            errorBorderColor = Color.Transparent
                        )
                    )
                }
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .fillMaxHeight(0.70F)
                        .background(
                            Color.White,
                            RoundedCornerShape(
                                topStart = 25.dp,
                                topEnd = 25.dp,
                                bottomStart = 0.dp,
                                bottomEnd = 0.dp
                            )
                        )
                        .padding(horizontal = 15.dp)
                        .align(Alignment.BottomCenter)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Spacer(modifier = Modifier.height(12.dp))
                        TransactionFieldView(
                            modifier = Modifier.fillMaxWidth()
                            .padding(vertical = 5.dp),
                            text = selectedPaymentType?.label ?: stringResource(id = R.string.paymentType),
                            color = LocalContentColor.current.copy(alpha = selectedPaymentType?.label?.let { 1F } ?: 0.5F),
                            onClick = {
                                onPaymentTypeClicked.invoke(selectedPaymentType)
                            },
                            isError = showPaymentTypeError,
                            errorView = {
                                if (showPaymentTypeError) {
                                    Text(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 10.dp),
                                        text = "Select a valid Payment Type", // FIXME: String resource
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        )
                        TransactionFieldView(
                            modifier = Modifier.fillMaxWidth()
                                .padding(vertical = 5.dp),
                            text = selectedCategory?.label ?: stringResource(id = R.string.category),
                            color = LocalContentColor.current.copy(alpha = selectedCategory?.label?.let { 1F } ?: 0.5F),
                            onClick = {
                                onCategoryClicked.invoke(selectedCategory)
                            },
                            isError = showCategoryError,
                            errorView = {
                                if (showCategoryError) {
                                    Text(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 10.dp),
                                        text = "Select a valid Category", // FIXME: String resource
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        )
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
                                Text(
                                    text = "Date",
                                    color = LocalContentColor.current.copy(alpha = 0.5F)
                                )
                            },
                            textStyle = LocalTextStyle.current,
                            onValueChange = { /* do nothing */ },
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = LocalContentColor.current.copy(alpha = 0.2F)
                            )
                        )
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = description,
                            maxLines = 4,
                            minLines = 3,
                            label = {
                                Text(
                                    text = "Description",
                                    color = LocalContentColor.current.copy(alpha = 0.5F)
                                )
                            },
                            textStyle = LocalTextStyle.current, onValueChange = {
                                description = it
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = LocalContentColor.current.copy(alpha = 0.2F)
                            )
                        )
                        MultiSelectChipsView(       //TODO: View attachment on click
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            selectedOptions = attachments,
                            onClick = {
                                showAddAttachmentDialog = true
                            },
                            selectionOptionView = {
                                Text(text = it.name)
                            },
                            onOptionCanceled = {
                                attachments.remove(it)
                            },
                            borderColor = LocalContentColor.current.copy(alpha = 0.2F)
                        )
                    }

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
                                transaction?.id ?: 212L,
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
                            .padding(vertical = 10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(text = "Add")
                    }
                }
            }
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

@Preview
@Composable
private fun AddTransactionFormPreview() {
    PreviewThemeWrapper {
        AddTransactionForm(
            onCategoryClicked = {},
            onTransactionTypeChanged = {},
            onPaymentTypeClicked = {},
            onNavigateBack = {}
        ) { _,_ -> }
    }
}