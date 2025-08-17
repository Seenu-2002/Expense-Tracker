package com.ajay.seenu.expensetracker.android.presentation.screeens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ajay.seenu.expensetracker.android.R
import com.ajay.seenu.expensetracker.android.domain.data.Transaction
import com.ajay.seenu.expensetracker.android.domain.data.UiState
import com.ajay.seenu.expensetracker.android.presentation.theme.LocalColors
import com.ajay.seenu.expensetracker.android.presentation.viewmodels.CategoriesListUiData
import com.ajay.seenu.expensetracker.android.presentation.viewmodels.CategoryScreenViewModel
import com.ajay.seenu.expensetracker.android.presentation.widgets.CategoryChangeConfirmationDialog
import com.ajay.seenu.expensetracker.android.presentation.widgets.CategoryRow
import com.ajay.seenu.expensetracker.android.presentation.widgets.ProgressDialog
import com.ajay.seenu.expensetracker.android.presentation.widgets.SlidingSwitch
import timber.log.Timber

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CategoryListScreen(
    onCategoryEdit: (Long) -> Unit,
    onCreateCategory: (Transaction.Type) -> Unit,
    onDeleteCategory: (id: Long, type: Transaction.Type, recordCount: Long) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: CategoryScreenViewModel = hiltViewModel()
) {
    val categoriesUiData by viewModel.categoriesUiData.collectAsStateWithLifecycle()
    val type by viewModel.transactionType.collectAsStateWithLifecycle()
    val transactionCountState by viewModel.transactionCount.collectAsStateWithLifecycle()
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var categoryToBeDeleted: Transaction.Category? by remember { mutableStateOf(null) }

    val context = LocalContext.current

    LaunchedEffect(categoriesUiData == UiState.Empty) {
        viewModel.getCategories()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ), title = {
                    Text(
                        modifier = Modifier.padding(horizontal = 4.dp),
                        text = stringResource(R.string.categories),
                        fontWeight = FontWeight.SemiBold
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
                        contentDescription = null
                    )
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
                    onClick = {
                        onCreateCategory.invoke(type)
                    },
                    shape = CircleShape,
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.add_transaction)
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            when (val state = categoriesUiData) {
                UiState.Loading, UiState.Empty -> {
                    CircularProgressIndicator()
                }

                is UiState.Success -> {
                    CategoryListScreenContent(
                        modifier = Modifier.matchParentSize(),
                        selectedType = type,
                        categoriesUiData = state.data,
                        onTypeChanged = { newType ->
                            Timber.d("Switching to type: $newType")
                            viewModel.changeType(newType)
                        },
                        onDelete = { category ->
                            val categories =
                                state.data.let { if (category.type == Transaction.Type.INCOME) it.incomeCategories else it.expenseCategories }

                            // Not allowing deletion if it's the last category of that type
                            if (categories.size <= 1) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.error_last_category_to_delete),
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@CategoryListScreenContent
                            }

                            viewModel.getTransactionCountByCategory(category)
                        },
                        onEdit = { category ->
                            onCategoryEdit(category.id)
                        }
                    )
                }

                is UiState.Failure -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Unable to load categories :: ${state.error}")
                    }
                }
            }

            when (val transactionCountState = transactionCountState) {
                UiState.Loading -> {
                    ProgressDialog()
                }

                is UiState.Success -> {
                    LaunchedEffect(Unit) {
                        Timber.e("Calling on delete")
                        val (count, category) = transactionCountState.data
                        if (count > 0) {
                            onDeleteCategory(
                                category.id,
                                category.type,
                                count
                            )
                        } else {
                            categoryToBeDeleted = category
                            showConfirmationDialog = true
                        }
                    }
                }

                is UiState.Failure -> {
                    Timber.e("Error fetching transaction count: ${transactionCountState.error}")
                    LaunchedEffect(Unit) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.error_last_category_to_delete),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                else -> {}
            }
        }

        if (showConfirmationDialog) {
            val categoryToBeDeleted = categoryToBeDeleted ?: return@Scaffold
            CategoryChangeConfirmationDialog(
                message = stringResource(
                    R.string.delete_category_message,
                    categoryToBeDeleted.label,
                ),
                onDismiss = {
                    showConfirmationDialog = false
                },
                onConfirm = {
                    viewModel.deleteCategory(categoryToBeDeleted.id)
                    Toast.makeText(
                        context,
                        context.getString(
                            R.string.delete_category_success,
                            categoryToBeDeleted.label
                        ),
                        Toast.LENGTH_SHORT
                    ).show()
                    showConfirmationDialog = false
                }
            )
        }

    }
}

@Composable
fun SwipeableCategoryRow(
    modifier: Modifier = Modifier,
    category: Transaction.Category,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    val swipeState =
        rememberSwipeToDismissBoxState(positionalThreshold = {
            it / 4
        }, confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.EndToStart -> {
                    onDelete()
                    false
                }

                SwipeToDismissBoxValue.StartToEnd -> {
                    onEdit()
                    false
                }

                SwipeToDismissBoxValue.Settled -> true
            }
        })

    lateinit var icon: @Composable () -> Unit
    lateinit var alignment: Alignment
    val color: Color

    when (swipeState.dismissDirection) {
        SwipeToDismissBoxValue.EndToStart,
        SwipeToDismissBoxValue.Settled -> {
            icon = {
                Icon(
                    modifier = Modifier
                        .padding(end = 15.dp),
                    imageVector = Icons.Default.Delete, contentDescription = "Delete"
                )
            }
            alignment = Alignment.CenterEnd
            color = LocalColors.current.expenseColor
        }

        SwipeToDismissBoxValue.StartToEnd -> {
            icon = {
                Icon(
                    modifier = Modifier
                        .padding(start = 15.dp),
                    painter = painterResource(id = R.drawable.edit_note),
                    contentDescription = "Clone"
                )
            }
            alignment = Alignment.CenterStart
            color = Color.Blue.copy(alpha = 0.3f)
        }
    }

    SwipeToDismissBox(
        modifier = modifier.animateContentSize(),
        state = swipeState,
        enableDismissFromEndToStart = true,
        enableDismissFromStartToEnd = true,
        backgroundContent = {
            Box(
                contentAlignment = alignment,
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
            ) {
                icon.invoke()
            }
        }
    ) {
        CategoryRow(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.surface)
                .padding(vertical = 4.dp, horizontal = 8.dp),
            category = category,
            clickable = false,
        )
    }
}

@Composable
fun CategoryListScreenContent(
    modifier: Modifier = Modifier,
    selectedType: Transaction.Type,
    categoriesUiData: CategoriesListUiData,
    onTypeChanged: (Transaction.Type) -> Unit,
    onDelete: (Transaction.Category) -> Unit,
    onEdit: (Transaction.Category) -> Unit,
) {

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val selectedCategories = if (selectedType == Transaction.Type.INCOME) {
            categoriesUiData.incomeCategories
        } else {
            categoriesUiData.expenseCategories
        }

        val values = Transaction.Type.entries.map { stringResource(it.stringRes) }
        SlidingSwitch(
            selectedValue = stringResource(selectedType.stringRes),
            values = values,
            modifier = Modifier.widthIn(max = 600.dp),
            shape = RoundedCornerShape(12.dp)
        ) { index, _ ->
            val type = Transaction.Type.entries[index]
            Timber.d("Switching to type: $type")
            onTypeChanged(type)
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (selectedCategories.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column {
                    Icon(
                        modifier = Modifier.size(100.dp),
                        painter = painterResource(id = R.drawable.icon_filter_list),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = stringResource(R.string.empty_category))
                }
            }
            return@Column
        }


        val pagerState = rememberPagerState(pageCount = {
            2
        }, initialPage = selectedType.ordinal)

        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }.collect { page ->
                Timber.d("Current page: $page")
                val type = Transaction.Type.entries[page]
                onTypeChanged(type)
            }
        }

        LaunchedEffect(selectedType) {
            if (pagerState.currentPage != selectedType.ordinal) {
                Timber.d("Switching to type: $selectedType")
                pagerState.animateScrollToPage(selectedType.ordinal, animationSpec = tween(500))
            }
        }

        HorizontalPager(
            state = pagerState,
        ) { page ->
            when (page) {
                0 -> {
                    CategoryList(
                        modifier = Modifier.fillMaxSize(),
                        categories = categoriesUiData.incomeCategories,
                        onDelete = onDelete,
                        onEdit = onEdit
                    )
                }

                1 -> {
                    CategoryList(
                        modifier = Modifier.fillMaxSize(),
                        categories = categoriesUiData.expenseCategories,
                        onDelete = onDelete,
                        onEdit = onEdit
                    )
                }
            }

        }
    }
}

@Composable
fun CategoryList(
    modifier: Modifier = Modifier,
    categories: List<Transaction.Category>,
    onDelete: (Transaction.Category) -> Unit,
    onEdit: (Transaction.Category) -> Unit
) {
    val lazyListState = rememberLazyListState()
    LazyColumn(
        modifier = modifier,
        state = lazyListState,
    ) {
        items(categories) { category ->
            SwipeableCategoryRow(
                modifier = Modifier, category = category, onDelete = {
                    onDelete(category)
                }, onEdit = {
                    onEdit(category)
                })
        }
    }
}