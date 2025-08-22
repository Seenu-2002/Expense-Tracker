package com.ajay.seenu.expensetracker.android.presentation.screeens.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ajay.seenu.expensetracker.android.presentation.viewmodels.BudgetViewModel
import com.ajay.seenu.expensetracker.entity.budget.BudgetWithSpending

@Composable
fun BudgetScreen(
    budgetViewModel: BudgetViewModel = hiltViewModel(),
    onCreateBudget: () -> Unit,
    onBudgetClick: (Long) -> Unit,
) {
    val budgets by budgetViewModel.budgets.collectAsState()
    val uiState by budgetViewModel.uiState.collectAsState()


    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            // Show error snackbar or toast
            budgetViewModel.clearError()
        }
    }

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            // Show success snackbar or toast
            budgetViewModel.clearMessage()
        }
    }

    BudgetListScreen(
        budgets = budgets,
        onCreateBudget = onCreateBudget,
        onBudgetClick = onBudgetClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetListScreen(
    budgets: List<BudgetWithSpending>,
    onCreateBudget: () -> Unit,
    onBudgetClick: (Long) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.background
            )
            .padding(horizontal = 16.dp)
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    "May",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            },
            navigationIcon = {
                IconButton(onClick = {}) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                    )
                }
            },
            actions = {
                IconButton(onClick = { }) {
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = "Next",
                    )
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Transparent
            ),
            windowInsets = WindowInsets()
        )

        if(budgets.isEmpty()) {
            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "You don't have a budget.",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Let's create one so you can control\nyour spendings.",
                    color = LocalContentColor.current.copy(alpha = 0.8f),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            Button(modifier = Modifier.fillMaxWidth()
                .padding(bottom = 10.dp),
                onClick = onCreateBudget,
                contentPadding = PaddingValues(vertical = 15.dp)) {
                Text(
                    "Create a budget",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        else {
            Column(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(budgets) { budgetWithSpending ->
                        BudgetCard(
                            budgetWithSpending = budgetWithSpending,
                            onClick = { onBudgetClick(budgetWithSpending.budget.id) }
                        )
                    }
                }
                Button(modifier = Modifier.fillMaxWidth()
                    .padding(bottom = 10.dp),
                    onClick = onCreateBudget,
                    contentPadding = PaddingValues(vertical = 15.dp)) {
                    Text(
                        "Create a budget",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

    }
}

@Composable
fun BudgetCard(
    budgetWithSpending: BudgetWithSpending,
    onClick: () -> Unit
) {
    val budget = budgetWithSpending.budget
    val progress = (budgetWithSpending.spentAmount / budget.amount).toFloat().coerceIn(0f, 1f)
    val isOverBudget = budgetWithSpending.isOverBudget

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(if (isOverBudget) MaterialTheme.colorScheme.error else Orange)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        budget.name,
                        fontSize = 14.sp,
                    )
                }

                if (isOverBudget) {
                    Box(
                        modifier = Modifier
                            .size(25.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.error),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "!",
                            fontSize = 10.sp,
                            color = Color.White,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Remaining $${String.format("%.0f", budgetWithSpending.remainingAmount.coerceAtLeast(0.0))}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (isOverBudget) MaterialTheme.colorScheme.error else Orange,
                trackColor = Gray100
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                "$${String.format("%.0f", budgetWithSpending.spentAmount)} of $${String.format("%.0f", budget.amount)}",
                fontSize = 12.sp,
            )
            if (isOverBudget) {
                Text(
                    "You've exceeded the limit",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}