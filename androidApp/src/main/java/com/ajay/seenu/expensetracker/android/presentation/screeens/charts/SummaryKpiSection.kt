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
import com.ajay.seenu.expensetracker.android.presentation.theme.AppTheme
import com.ajay.seenu.expensetracker.android.presentation.theme.LocalCurrencySymbol
import com.ajay.seenu.expensetracker.android.presentation.viewmodels.chart_viewmodels.SummaryKpi
import com.ajay.seenu.expensetracker.android.presentation.viewmodels.chart_viewmodels.SummaryKpiViewModel
import com.ajay.seenu.expensetracker.android.util.asCurrency
import com.ajay.seenu.expensetracker.domain.model.DateFilter

@Composable
fun SummaryKpiSection(
    modifier: Modifier = Modifier,
    filter: DateFilter = DateFilter.ThisMonth,
    viewModel: SummaryKpiViewModel = hiltViewModel(),
) {
    LaunchedEffect(filter) { viewModel.setFilter(filter) }
    val kpi by viewModel.kpi.collectAsStateWithLifecycle()
    SummaryKpiGrid(kpi, modifier)
}

@Composable
private fun SummaryKpiGrid(kpi: SummaryKpi, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            KpiCard(
                label = "Income",
                amount = kpi.income,
                accent = AppTheme.colors.income,
                modifier = Modifier.weight(1f),
            )
            KpiCard(
                label = "Expense",
                amount = kpi.expense,
                accent = AppTheme.colors.expense,
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            KpiCard(
                label = "Net",
                amount = kpi.net,
                accent = if (kpi.net >= 0.0) AppTheme.colors.income else AppTheme.colors.expense,
                modifier = Modifier.weight(1f),
            )
            SavingsRateCard(
                rate = kpi.savingsRate,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun KpiCard(
    label: String,
    amount: Double,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(width = 4.dp, height = 32.dp)
                .background(accent, RoundedCornerShape(2.dp)),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
            )
            Text(
                text = amount.asCurrency(LocalCurrencySymbol.current),
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
            )
        }
    }
}

@Composable
private fun SavingsRateCard(rate: Double, modifier: Modifier = Modifier) {
    val accent = when {
        rate >= 20.0 -> AppTheme.colors.income
        rate >= 0.0 -> AppTheme.colors.warning
        else -> AppTheme.colors.expense
    }
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(width = 4.dp, height = 32.dp)
                .background(accent, RoundedCornerShape(2.dp)),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = "Savings rate",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
            )
            Text(
                text = "${"%.1f".format(rate)}%",
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
            )
        }
    }
}

