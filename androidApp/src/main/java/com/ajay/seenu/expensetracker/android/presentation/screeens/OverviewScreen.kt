package com.ajay.seenu.expensetracker.android.presentation.screeens

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ajay.seenu.expensetracker.android.presentation.viewmodels.OverviewScreenViewModel
import com.ajay.seenu.expensetracker.android.presentation.widgets.OverviewCard
import com.ajay.seenu.expensetracker.android.presentation.widgets.TransactionPreviewRow

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OverviewScreen(
    viewModel: OverviewScreenViewModel = hiltViewModel(),
    onCloneTransaction: (Long) -> Unit,
) {
    val recentTransactions by viewModel.recentTransactions.collectAsStateWithLifecycle()
    val overallData by viewModel.overallData.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val hasMoreData by viewModel.hasMoreData.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.getOverallData()
        viewModel.getRecentTransactions()
    }

    Scaffold(
        topBar = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Welcome, $userName",
                    modifier = Modifier.padding(vertical = 12.dp),
                    fontSize = 24.sp
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
        ) {

            if (recentTransactions.isEmpty()) {
                return@Column // FIXME: Have to handle empty screen state
            }

            // FIXME: Have to be replaced with UIState instead of null check
            overallData?.let { 
                OverviewCard(modifier = Modifier.fillMaxWidth(), data = it)
            }
            val listState = rememberLazyListState()

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
            ) {

                recentTransactions.forEach {
                    stickyHeader {
                        Text(
                            modifier = Modifier
                                .fillParentMaxWidth()
                                .background(MaterialTheme.colorScheme.background)
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            text = it.dateLabel
                        )
                    }
                    items(it.transactions,
                        key = { transaction ->
                              transaction.id
                        },
                        itemContent = {
                        TransactionPreviewRow(Modifier.fillMaxWidth(), it,
                            onDelete = {
                                //FIXME: Getting deleted by data not updated live
                                viewModel.deleteTransaction(it.id)
                            },
                            onClone = {
                                onCloneTransaction.invoke(it.id)
                            }
                        )
                    })
                }
            }

            if (hasMoreData) {
                val isAtBottom = !listState.canScrollForward
                LaunchedEffect(isAtBottom) {
                    if (isAtBottom) {
                        Log.e("TEST", "End reached")
                        viewModel.getNextPageTransactions()
                    }
                }
            }
        }
    }
}