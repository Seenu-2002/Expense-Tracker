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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ajay.seenu.expensetracker.android.presentation.screeens.InsufficientDataCard
import com.ajay.seenu.expensetracker.android.presentation.theme.LocalCurrencySymbol
import com.ajay.seenu.expensetracker.android.presentation.viewmodels.chart_viewmodels.RankedCategory
import com.ajay.seenu.expensetracker.android.presentation.viewmodels.chart_viewmodels.TopCategoriesState
import com.ajay.seenu.expensetracker.android.presentation.viewmodels.chart_viewmodels.TopSpendingCategoriesViewModel
import com.ajay.seenu.expensetracker.android.util.asCurrency
import com.ajay.seenu.expensetracker.domain.model.DateFilter

@Composable
fun TopSpendingCategoriesSection(
    modifier: Modifier = Modifier,
    filter: DateFilter = DateFilter.ThisMonth,
    viewModel: TopSpendingCategoriesViewModel = hiltViewModel(),
) {
    LaunchedEffect(filter) { viewModel.setFilter(filter) }
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (val s = state) {
        TopCategoriesState.Loading -> {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .height(160.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text("Loading…", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        TopCategoriesState.Empty -> {
            InsufficientDataCard(
                modifier
                    .fillMaxWidth()
                    .height(160.dp)
            )
        }
        is TopCategoriesState.Ready -> RankedList(s.rows, modifier)
    }
}

@Composable
private fun RankedList(rows: List<RankedCategory>, modifier: Modifier = Modifier) {
    val topAmount = rows.first().amount.coerceAtLeast(1.0)
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        rows.forEach { row ->
            CategoryRow(row, fillFraction = (row.amount / topAmount).toFloat().coerceIn(0f, 1f))
        }
    }
}

@Composable
private fun CategoryRow(row: RankedCategory, fillFraction: Float) {
    val tint = Color(row.category.color)
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(tint.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(row.category.iconRes),
                    contentDescription = row.category.label,
                    tint = tint,
                    modifier = Modifier.size(16.dp),
                )
            }
            Spacer(modifier = Modifier.size(10.dp))
            Text(
                text = row.category.label,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp,
            )
            Text(
                text = row.amount.asCurrency(LocalCurrencySymbol.current),
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
            )
        }
        Spacer(modifier = Modifier.size(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fillFraction)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(tint)
                )
            }
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "${"%.0f".format(row.percentOfTotal)}%",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
            )
        }
    }
}
