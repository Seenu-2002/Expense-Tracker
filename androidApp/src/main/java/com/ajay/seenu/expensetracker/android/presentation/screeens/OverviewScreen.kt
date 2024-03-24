package com.ajay.seenu.expensetracker.android.presentation.screeens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ajay.seenu.expensetracker.android.presentation.viewmodels.OverviewScreenViewModel
import com.ajay.seenu.expensetracker.android.presentation.viewmodels.Transaction
import com.ajay.seenu.expensetracker.android.presentation.widgets.OverviewCard
import com.ajay.seenu.expensetracker.android.presentation.widgets.TransactionPreviewRow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OverviewScreen(
    navController: NavController,
    viewModel: OverviewScreenViewModel = hiltViewModel()
) {
    val recentTransactions by viewModel.recentTransactions.collectAsState()
    val overallData by viewModel.overallData.collectAsState()
    val userName by viewModel.userName.collectAsState()

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
            // FIXME: Have to be replaced with UIState instead of null check
            overallData?.let { 
                OverviewCard(modifier = Modifier.fillMaxWidth(), data = it)
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {

                recentTransactions.entries.forEach {
                    stickyHeader {
                        Text(
                            modifier = Modifier
                                .fillParentMaxWidth()
                                .background(MaterialTheme.colorScheme.background)
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            text = it.key
                        )
                    }
                    items(it.value, itemContent = {
                        TransactionPreviewRow(Modifier.fillMaxWidth(), it)
                    })
                }
            }
        }
    }
}