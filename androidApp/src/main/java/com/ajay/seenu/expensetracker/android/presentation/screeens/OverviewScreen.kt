package com.ajay.seenu.expensetracker.android.presentation.screeens

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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OverviewScreen(navController: NavController, viewModel: OverviewScreenViewModel = hiltViewModel()) {
    val spendSoFar by viewModel.spendSoFar.collectAsState()
    val recentTransactions by viewModel.recentTransactions.collectAsState()
    val userName by viewModel.userName.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getRecentTransactions()
    }

    Scaffold(
        topBar = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(text = "Welcome, $userName", modifier = Modifier.padding(vertical = 12.dp), fontSize = 24.sp)
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues = paddingValues)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(8.dp)
                    .background(shape = RoundedCornerShape(8.dp), color = Color(0xFFC8C8C8)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Spent So Far", fontSize = 22.sp, fontWeight = FontWeight.Medium)
                Text(text = spendSoFar)
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(recentTransactions, itemContent = {
                    TransactionRow(transaction = it)
                })
            }
        }
    }
}

@Preview
@Composable
fun TransactionRow(
    formatter: SimpleDateFormat = SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH),
    transaction: Transaction = Transaction(
        Transaction.Type.INCOME,
        "Test Transaction",
        100.0,
        "Netflix",
        Date(2423423L)
    )
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .background(shape = RoundedCornerShape(8.dp), color = Color(0xFF29756F))
            .padding(8.dp)
    ) {
        val (category, date, amount) = createRefs()
        Text(
            text = transaction.category,
            color = Color.Red,
            modifier = Modifier.constrainAs(category) {
                top.linkTo(parent.top)
                linkTo(start = parent.start, end = amount.start, bias = 0F)
                bottom.linkTo(date.top)
            })
        Text(
            text = formatter.format(transaction.date),
            color = Color.White,
            modifier = Modifier.constrainAs(date) {
                top.linkTo(category.bottom)
                linkTo(start = parent.start, end = amount.start, bias = 0F)
                bottom.linkTo(parent.bottom)
            })
        Text(text = "Rs. ${transaction.amount}",
            color = Color.White, modifier = Modifier.constrainAs(amount) {
                top.linkTo(parent.top)
                end.linkTo(parent.end, 8.dp)
                bottom.linkTo(parent.bottom)
            })
    }
}