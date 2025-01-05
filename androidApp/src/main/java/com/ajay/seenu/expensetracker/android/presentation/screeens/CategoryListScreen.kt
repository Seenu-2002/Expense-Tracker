package com.ajay.seenu.expensetracker.android.presentation.screeens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ajay.seenu.expensetracker.android.R
import com.ajay.seenu.expensetracker.android.domain.data.Transaction
import com.ajay.seenu.expensetracker.android.presentation.viewmodels.CategoryScreenViewModel
import com.ajay.seenu.expensetracker.android.presentation.widgets.SlidingSwitch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CategoryListScreen(
    categoryDetailScreen: (Long?) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: CategoryScreenViewModel = hiltViewModel()
) {
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val type by viewModel.transactionType.collectAsStateWithLifecycle()

    val listState = rememberLazyListState()

    LaunchedEffect(type) {
        viewModel.getCategories(type)
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
            }
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
                        categoryDetailScreen.invoke(null)
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
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

            if (categories.isEmpty()) {
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
                items(categories) {
                    CategoryRow(category = it) {

                    }
                }
            }
        }
    }
}

@Composable
fun CategoryRow(
    modifier: Modifier = Modifier,
    category: Transaction.Category,
    onClicked: (Transaction.Category) -> Unit
) {
    Row(modifier = modifier) {
        CategoryIconItem(
            modifier = Modifier
                .size(52.dp)
                .padding(4.dp),
            res = category.res,
            iconBackground = category.color
        )
        Text(
            modifier = Modifier
                .weight(1F)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .clickable {
                    onClicked(category)
                },
            text = category.label
        )
    }
}