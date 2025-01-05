@file:OptIn(ExperimentalMaterial3Api::class)

package com.ajay.seenu.expensetracker.android.presentation.screeens

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ajay.seenu.expensetracker.android.R
import com.ajay.seenu.expensetracker.android.domain.data.Transaction
import com.ajay.seenu.expensetracker.android.presentation.viewmodels.AddTransactionViewModel
import com.ajay.seenu.expensetracker.android.presentation.widgets.AddTransactionForm
import com.ajay.seenu.expensetracker.entity.PaymentType
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    onNavigateBack: () -> Unit,
    cloneId: Long? = null,
    viewModel: AddTransactionViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.getCategories(Transaction.Type.INCOME)
        cloneId?.let { id ->
            viewModel.getTransaction(id)
        }
    }
    val context = LocalContext.current
    val transaction by viewModel.transaction.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    var showForm by rememberSaveable { mutableStateOf(false) }
    var selectedCategory: Transaction.Category? by remember {
        mutableStateOf(null)
    }

    var selectedPaymentType: PaymentType by remember {
        mutableStateOf(PaymentType.UPI)
    }

    var showCategoriesBottomSheet by remember {
        mutableStateOf(false)
    }

    var showPaymentTypeBottomSheet by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(transaction) {
        if (cloneId == null)
            showForm = true
        else {
            transaction?.let {
                selectedCategory = it.category
                showForm = true
            }
        }
    }

    if (showForm) {
        AddTransactionForm(
            modifier = Modifier
                .padding(horizontal = 48.dp, vertical = 32.dp),
            transaction = transaction,
            onCategoryClicked = {
                showCategoriesBottomSheet = true
            },
            selectedCategory = selectedCategory,
            selectedPaymentType = selectedPaymentType,
            onTransactionTypeChanged = { type ->
                selectedCategory = null
                viewModel.getCategories(type)
            },
            onPaymentTypeClicked = {
                showPaymentTypeBottomSheet = true
            },
            onNavigateBack = onNavigateBack
        ) { transaction, attachments ->
            viewModel.addTransaction(context, transaction, attachments)
            Toast.makeText(context, "Transaction added Successfully!", Toast.LENGTH_SHORT).show()
            onNavigateBack.invoke()
        }
    }

    val categoriesBottomSheetState = rememberModalBottomSheetState(true)
    val paymentTypeBottomSheetState = rememberModalBottomSheetState(true)
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(categoriesBottomSheetState) {
        snapshotFlow { categoriesBottomSheetState.isVisible }.collect { it ->
            showCategoriesBottomSheet = it
        }
    }

    LaunchedEffect(paymentTypeBottomSheetState) {
        snapshotFlow { paymentTypeBottomSheetState.isVisible }.collect { it ->
            showPaymentTypeBottomSheet = it
        }
    }

    if (showCategoriesBottomSheet) {
        CategoryBottomSheet(state = categoriesBottomSheetState, onDismiss = {
            focusManager.clearFocus(true)
            showCategoriesBottomSheet = false
        }, categories = categories) { category ->
            selectedCategory = category
            scope.launch {
                categoriesBottomSheetState.hide()
            }
        }
    }

    if (showPaymentTypeBottomSheet) {
        PaymentTypeBottomSheet(
            state = paymentTypeBottomSheetState,
            onDismiss = {
                focusManager.clearFocus(true)
                showPaymentTypeBottomSheet = false
            },
            paymentTypes = PaymentType.entries
        ) { type ->
            selectedPaymentType = type
            scope.launch {
                paymentTypeBottomSheetState.hide()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryBottomSheet(
    state: SheetState,
    onDismiss: () -> Unit,
    categories: List<Transaction.Category>,
    onCategorySelected: (Transaction.Category) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = state,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        LazyColumn(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.animateContentSize(
                animationSpec = TweenSpec()
            )
        ) {
            items(categories.size) {
                val category = categories[it]
                CategoryRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp, horizontal = 8.dp),
                    category,
                ) { selectedCategory ->
                    onCategorySelected(selectedCategory)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExpandableRowPreview() {
    val category = Transaction.Category(
        1212,
        Transaction.Type.INCOME,
        "Salary daw!",
        Color.Red,
        R.drawable.attach_money,
    )
    CategoryRow(category = category) {

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentTypeBottomSheet(
    state: SheetState,
    onDismiss: () -> Unit,
    paymentTypes: List<PaymentType>,
    onPaymentTypeSelected: (PaymentType) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = state
    ) {
        LazyColumn {
            items(paymentTypes.size) {
                val type = paymentTypes[it]
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onPaymentTypeSelected(type)
                        }
                        .padding(vertical = 6.dp)
                        .padding(start = 12.dp),
                    text = type.label,
                )
            }
        }
    }
}
