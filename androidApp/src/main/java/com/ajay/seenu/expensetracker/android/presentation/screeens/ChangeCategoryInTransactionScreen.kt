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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.ajay.seenu.expensetracker.android.domain.data.Error
import com.ajay.seenu.expensetracker.android.domain.data.Transaction
import com.ajay.seenu.expensetracker.android.domain.data.UiState
import com.ajay.seenu.expensetracker.android.presentation.viewmodels.ChangeCategoryInTransactionViewModel
import com.ajay.seenu.expensetracker.android.presentation.widgets.CategoryChangeConfirmationDialog
import com.ajay.seenu.expensetracker.android.presentation.widgets.CategoryRow
import com.ajay.seenu.expensetracker.android.presentation.widgets.ProgressDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeCategoryInTransactionScreen(
    onNavigateBack: () -> Unit,
    categoryIdToBeDeleted: Long,
    type: Transaction.Type,
    transactionCount: Long
) {
    val viewmodel: ChangeCategoryInTransactionViewModel = hiltViewModel()
    val categoriesState by viewmodel.categories.collectAsStateWithLifecycle()
    val updateStatus by viewmodel.updateStatus.collectAsStateWithLifecycle()
    val context = LocalContext.current

    lateinit var categoryIdToBeReplacedWith: Transaction.Category
    var showConfirmationDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (categoriesState == UiState.Empty) {
            viewmodel.init(
                type = type,
                categoryToBeDeletedId = categoryIdToBeDeleted
            )
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.change_category),
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
            when (val state = categoriesState) {
                UiState.Loading, UiState.Empty -> {
                    CircularProgressIndicator()
                }

                is UiState.Success -> {
                    val categoryToBeDeleted = viewmodel.categoryToBeDeleted
                    ChangeCategoryInTransactionContent(
                        modifier = Modifier.matchParentSize(),
                        recordCount = transactionCount,
                        categoryToBeDeleted = categoryToBeDeleted,
                        categories = state.data,
                        onCategorySelected = { selectedCategory ->
                            categoryIdToBeReplacedWith = selectedCategory
                            showConfirmationDialog = true
                        }
                    )
                }

                is UiState.Failure -> {
                    if (state.error is Error.CategoryNotFound) {
                        Toast.makeText(
                            context,
                            R.string.error_category_not_found,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            context,
                            "Unable to change categories :: ${state.error}",
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
                            R.string.delete_category_success,
                            viewmodel.categoryToBeDeleted.label
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
                        "Unable to update category :: ${updateStatus.error}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {}
            }
        }

        if (showConfirmationDialog) {
            CategoryChangeConfirmationDialog(
                message = stringResource(
                    R.string.replace_category_message,
                    viewmodel.categoryToBeDeleted.label,
                    categoryIdToBeReplacedWith.label
                ),
                onDismiss = {
                    showConfirmationDialog = false
                },
                onConfirm = {
                    viewmodel.updateCategory(categoryIdToBeReplacedWith)
                    showConfirmationDialog = false
                }
            )
        }
    }
}

@Composable
fun ChangeCategoryInTransactionContent(
    modifier: Modifier = Modifier,
    recordCount: Long,
    categoryToBeDeleted: Transaction.Category,
    categories: List<Transaction.Category>,
    onCategorySelected: (Transaction.Category) -> Unit
) {
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
                    R.string.replace_category_info,
                    categoryToBeDeleted.label,
                    recordCount
                ),
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F)
        ) {
            items(categories) { category ->
                CategoryRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    category = category,
                    clickable = true,
                    onClicked = onCategorySelected
                )
            }
        }
    }
}