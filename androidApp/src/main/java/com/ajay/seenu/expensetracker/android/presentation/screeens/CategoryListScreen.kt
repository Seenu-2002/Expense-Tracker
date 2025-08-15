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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ajay.seenu.expensetracker.android.R
import com.ajay.seenu.expensetracker.android.domain.data.Transaction
import com.ajay.seenu.expensetracker.android.domain.data.UiState
import com.ajay.seenu.expensetracker.android.presentation.theme.LocalColors
import com.ajay.seenu.expensetracker.android.presentation.viewmodels.CategoryScreenViewModel
import com.ajay.seenu.expensetracker.android.presentation.widgets.CategoryRow
import com.ajay.seenu.expensetracker.android.presentation.widgets.SlidingSwitch
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CategoryListScreen(
    onCategoryEdit: (Long) -> Unit,
    onCreateCategory: (Transaction.Type) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: CategoryScreenViewModel = hiltViewModel()
) {
    val categoriesState by viewModel.categories.collectAsStateWithLifecycle()
    val type by viewModel.transactionType.collectAsStateWithLifecycle()

    val listState = rememberLazyListState()
    val context = LocalContext.current

    LaunchedEffect(type) {
        viewModel.getCategories(type)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ), title = {
                    Text(
                        modifier = Modifier.padding(horizontal = 4.dp),
                        text = stringResource(R.string.categories)
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
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {

            when (val state = categoriesState) {
                UiState.Loading, UiState.Empty -> {
                    CircularProgressIndicator()
                }

                is UiState.Success -> {
                    Column(
                        modifier = Modifier
                            .matchParentSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        val values = Transaction.Type.entries.map { stringResource(it.stringRes) }
                        SlidingSwitch(
                            selectedValue = stringResource(type.stringRes),
                            values = values,
                            modifier = Modifier.widthIn(max = 600.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) { index, _ ->
                            viewModel.changeType(Transaction.Type.entries[index])
                        }

                        if (state.data.isEmpty()) {
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
                            return@Scaffold
                        }

                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            items(state.data) { category ->
                                SwipeableCategoryRow(category = category, onDelete = {
                                    if (state.data.size <= 1) {
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.error_last_category_to_delete),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    viewModel.deleteCategory(category.id)
                                }, onEdit = {
                                    onCategoryEdit(category.id)
                                })
                            }
                        }
                    }
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
        androidx.compose.material3.rememberSwipeToDismissBoxState(positionalThreshold = {
            it / 4
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
    when (swipeState.currentValue) {
        SwipeToDismissBoxValue.EndToStart -> {
            onDelete.invoke()
        }

        SwipeToDismissBoxValue.StartToEnd -> {
            LaunchedEffect(swipeState) {
                onEdit.invoke()
                delay(100)
                swipeState.snapTo(SwipeToDismissBoxValue.Settled)
            }
        }

        SwipeToDismissBoxValue.Settled -> {
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
                .background(color = MaterialTheme.colorScheme.surface),
            category = category,
            clickable = false
        )
    }
}