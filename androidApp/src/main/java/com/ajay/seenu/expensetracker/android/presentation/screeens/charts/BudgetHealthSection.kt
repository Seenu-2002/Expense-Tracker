package com.ajay.seenu.expensetracker.android.presentation.screeens.charts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ajay.seenu.expensetracker.android.presentation.common.ChartDefaults
import com.ajay.seenu.expensetracker.android.presentation.theme.LocalCurrencySymbol
import com.ajay.seenu.expensetracker.android.presentation.viewmodels.chart_viewmodels.BudgetHealthViewModel
import com.ajay.seenu.expensetracker.android.util.asCurrency
import com.ajay.seenu.expensetracker.domain.model.DateFilter
import com.ajay.seenu.expensetracker.domain.model.budget.BudgetWithSpending

@Composable
fun BudgetHealthSection(
    modifier: Modifier = Modifier,
    filter: DateFilter = DateFilter.ThisMonth,
    viewModel: BudgetHealthViewModel = hiltViewModel(),
) {
    LaunchedEffect(filter) { viewModel.setFilter(filter) }
    val health by viewModel.health.collectAsStateWithLifecycle()

    if (!health.hasBudgets) {
        EmptyBudgets(modifier.fillMaxWidth())
        return
    }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            StatTile("Budgeted", health.totalBudgeted.asCurrency(LocalCurrencySymbol.current), Modifier.weight(1f))
            StatTile("Spent", health.totalSpent.asCurrency(LocalCurrencySymbol.current), Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            val remainingColor = if (health.totalRemaining < 0.0) ChartDefaults.expenseColor else MaterialTheme.colorScheme.onSurface
            StatTile(
                label = "Remaining",
                value = health.totalRemaining.asCurrency(LocalCurrencySymbol.current),
                valueColor = remainingColor,
                modifier = Modifier.weight(1f),
            )
            StatTile(
                label = "Used",
                value = "${"%.0f".format(health.overallUsedPercent)}%",
                modifier = Modifier.weight(1f),
            )
        }

        StatusRow(atRisk = health.atRiskCount, overBudget = health.overBudgetCount)

        if (health.topOffenders.isNotEmpty()) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Needs attention",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            health.topOffenders.forEach { OffenderRow(it) }
        }
    }
}

@Composable
private fun StatTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = valueColor)
    }
}

@Composable
private fun StatusRow(atRisk: Int, overBudget: Int) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        StatusChip(
            label = "At risk",
            count = atRisk,
            tint = if (atRisk > 0) Color(0xFFFFA726) else MaterialTheme.colorScheme.onSurfaceVariant,
        )
        StatusChip(
            label = "Over budget",
            count = overBudget,
            tint = if (overBudget > 0) ChartDefaults.expenseColor else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun StatusChip(label: String, count: Int, tint: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(tint, RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "$label: $count",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun OffenderRow(row: BudgetWithSpending) {
    val percentText = "${"%.0f".format(row.percentageUsed)}%"
    val isOver = row.isOverBudget
    val tint = if (isOver) ChartDefaults.expenseColor else Color(0xFFFFA726)
    val symbol = if (isOver) "!" else "⚠"

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(tint.copy(alpha = 0.18f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = symbol, color = tint, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = row.budget.name,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp,
            )
            Text(
                text = percentText,
                color = tint,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        val barFraction = (row.percentageUsed / 100.0).coerceIn(0.0, 1.0).toFloat()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(barFraction)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(tint)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun EmptyBudgets(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "No active budgets",
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Set one up from the Budget tab.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
        )
    }
}
