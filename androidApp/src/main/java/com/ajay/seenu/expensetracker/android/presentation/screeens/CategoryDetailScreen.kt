package com.ajay.seenu.expensetracker.android.presentation.screeens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ajay.seenu.expensetracker.android.presentation.viewmodels.CategoryScreenViewModel
import com.ajay.seenu.expensetracker.entity.TransactionType
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailScreen(
    onNavigateBack: () -> Unit,
    categoryId: Long?,
    viewModel: CategoryScreenViewModel = hiltViewModel()
) {
    val category by viewModel.category.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    var categoryLabel by remember {
        mutableStateOf(category?.label ?: "")
    }

    val categoryInteractionSource = remember {
        MutableInteractionSource()
    }

    val categoryTypeInteractionSource = remember {
        MutableInteractionSource()
    }

    var showCategoryError by remember {
        mutableStateOf(false)
    }

    var showTransactionTypesBottomSheet by remember {
        mutableStateOf(false)
    }

    val transactionTypesBottomSheetState = rememberModalBottomSheetState(true)

    var categoryTransactionType by remember {
        mutableStateOf(category?.type ?: TransactionType.INCOME)
    }

    LaunchedEffect(category != null) {
        categoryLabel = category?.label ?: ""
        categoryTransactionType = category?.type ?: TransactionType.INCOME
    }

    LaunchedEffect(transactionTypesBottomSheetState) {
        snapshotFlow { transactionTypesBottomSheetState.isVisible }.collect {
            showTransactionTypesBottomSheet = it
        }
    }

    if (categoryInteractionSource.collectIsPressedAsState().value) {
        showCategoryError = false
    }

    if (categoryTypeInteractionSource.collectIsPressedAsState().value) {
        showTransactionTypesBottomSheet = true
    }

    LaunchedEffect(Unit) {
        if (categoryId != null) {
            viewModel.getCategory(categoryId)
        }
    }

    if (showTransactionTypesBottomSheet) {
        TransactionBottomSheet (state = transactionTypesBottomSheetState, onDismiss = {
            focusManager.clearFocus(true)
            showTransactionTypesBottomSheet = false
        }, transactionTypes = TransactionType.entries) { transactionType ->
            categoryTransactionType = transactionType
            scope.launch {
                transactionTypesBottomSheetState.hide()
            }
        }
    }

    Scaffold(
        topBar = {
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
                        text = category?.label ?: "Add Category"
                    )
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
                        contentDescription = "Back"
                    )
                })
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = categoryLabel,
                maxLines = 2,
                minLines = 1,
                label = {
                    Text(text = "Category name")
                },
                interactionSource = categoryInteractionSource,
                supportingText = {
                    if (showCategoryError) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp),
                            text = "Enter a valid Category name", // FIXME: String resource
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                isError = showCategoryError,
                textStyle = LocalTextStyle.current, onValueChange = {
                    categoryLabel = it
                })
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                interactionSource = categoryTypeInteractionSource ,
                value = categoryTransactionType.name,
                readOnly = true,
                label = {
                    Text(text = "Transaction type")
                },
                textStyle = LocalTextStyle.current,
                onValueChange = {})
            Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                OutlinedButton(
                    onClick = {
                        if( categoryLabel.isEmpty() ) {
                            showCategoryError = true
                        } else {
                            if( categoryId == null ) {
                                viewModel.addCategory( categoryLabel, categoryTransactionType )
                            } else {
                                viewModel.updateCategory( categoryId, categoryLabel, categoryTransactionType )
                            }
                            onNavigateBack.invoke()
                        }
                    }
                ) {
                    Text("SAVE")
                }
                if( categoryId != null ) {
                    OutlinedButton(
                        onClick = {
                            viewModel.deleteCategory(categoryId)
                            onNavigateBack.invoke()
                        }
                    ) {
                        Text("DELETE")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionBottomSheet(
    state: SheetState,
    onDismiss: () -> Unit,
    transactionTypes: List<TransactionType>,
    onTransactionTypeSelected: (TransactionType) -> Unit,
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
            items(transactionTypes.size) {
                val transactionType = transactionTypes[it]
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onTransactionTypeSelected.invoke( transactionType )
                        }
                        .padding(vertical = 4.dp, horizontal = 8.dp),
                ) {
                    Text( text = transactionType.name )
                }
            }
        }
    }
}