@file:OptIn(ExperimentalMaterial3Api::class)

package com.ajay.seenu.expensetracker.android.presentation.screeens

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ajay.seenu.expensetracker.android.R
import com.ajay.seenu.expensetracker.android.presentation.components.CategoryRow
import com.ajay.seenu.expensetracker.android.presentation.components.TransactionForm
import com.ajay.seenu.expensetracker.android.presentation.state.TransactionMode
import com.ajay.seenu.expensetracker.android.presentation.viewmodels.AddTransactionViewModel
import com.ajay.seenu.expensetracker.domain.model.Category
import com.ajay.seenu.expensetracker.domain.model.TransactionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    transactionMode: TransactionMode = TransactionMode.New,
    viewModel: AddTransactionViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.getCategories(TransactionType.INCOME)
        when (transactionMode) {
            TransactionMode.New -> {}
            is TransactionMode.Clone -> {
                viewModel.getTransaction(transactionMode.id)
            }

            is TransactionMode.Edit -> {
                viewModel.getTransaction(transactionMode.id)
            }
        }
    }

    val context = LocalContext.current
    val transaction by viewModel.transaction.collectAsStateWithLifecycle()
    val attachments by viewModel.attachments.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    var showForm by rememberSaveable { mutableStateOf(false) }
    var selectedCategory: Category? by remember {
        mutableStateOf(null)
    }

    var showCategoriesBottomSheet by remember {
        mutableStateOf(false)
    }

    var showAccountBottomSheet by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(transaction) {
        when (transactionMode) {
            TransactionMode.New -> {
                showForm = true
            }

            is TransactionMode.Clone -> {
                transaction?.let {
                    selectedCategory = it.category
                    showForm = true
                }
            }

            is TransactionMode.Edit -> {
                transaction?.let {
                    selectedCategory = it.category
                    showForm = true
                }
            }
        }
    }

    if (showForm) {
        TransactionForm(
            modifier = Modifier
                .fillMaxSize(),
            transactionMode = transactionMode,
            transaction = transaction,
            existingAttachments = attachments,
            onCategoryClicked = {
                showCategoriesBottomSheet = true
            },
            selectedCategory = selectedCategory,
            onTransactionTypeChanged = { type ->
                selectedCategory = null
                viewModel.getCategories(type)
            },
            onAccountClicked = {
                showAccountBottomSheet = true
            },
            onNavigateBack = onNavigateBack,
            onAdd = { newTransaction, newAttachments ->
                if (transactionMode is TransactionMode.Edit)
                    viewModel.updateTransaction(newTransaction, newAttachments)
                else
                    viewModel.addTransaction(newTransaction, newAttachments)
                Toast.makeText(context, "Transaction added Successfully!", Toast.LENGTH_SHORT).show()
                onNavigateBack.invoke()
            }
        )
    }

    val categoriesBottomSheetState = rememberModalBottomSheetState(true)
    val paymentTypeBottomSheetState = rememberModalBottomSheetState(true) // TODO: Change it to category
    val focusManager = LocalFocusManager.current

    if (showCategoriesBottomSheet) {
        CategoryBottomSheet(state = categoriesBottomSheetState, onDismiss = {
            focusManager.clearFocus(true)
            showCategoriesBottomSheet = false
        }, categories = categories) { category ->
            selectedCategory = category
            showCategoriesBottomSheet = false
        }
    }

    if (showAccountBottomSheet) {
        TODO()
//        PaymentTypeBottomSheet(
//            selectedPaymentType = selectedPaymentType,
//            state = paymentTypeBottomSheetState,
//            onDismiss = {
//                focusManager.clearFocus(true)
//                showPaymentTypeBottomSheet = false
//            },
//            paymentTypes = PaymentType.entries
//        ) { type ->
//            selectedPaymentType = type
//            showPaymentTypeBottomSheet = false
//        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryBottomSheet(
    state: SheetState,
    onDismiss: () -> Unit,
    categories: List<Category>,
    onCategorySelected: (Category) -> Unit,
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
                    iconBoxSize = 42.dp,
                    clickable = true,
                    iconSize = 20.dp
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
    val category = Category(
        1212,
        TransactionType.INCOME,
        "Salary daw!",
        Color.Red.toArgb().toLong(),
        R.drawable.attach_money,
    )
    CategoryRow(category = category) {

    }
}