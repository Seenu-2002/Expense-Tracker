package com.ajay.seenu.expensetracker.android.presentation.screeens.charts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ajay.seenu.expensetracker.android.presentation.components.SlidingSwitch
import com.ajay.seenu.expensetracker.android.presentation.screeens.AnalyticsLegendRowData
import com.ajay.seenu.expensetracker.android.presentation.screeens.InsufficientDataCard
import com.ajay.seenu.expensetracker.android.presentation.state.UiState
import com.ajay.seenu.expensetracker.android.presentation.theme.LocalCurrencySymbol
import com.ajay.seenu.expensetracker.android.presentation.viewmodels.SimpleAnalyticsViewModel
import com.ajay.seenu.expensetracker.android.util.asCurrency
import com.ajay.seenu.expensetracker.android.util.getColor
import com.ajay.seenu.expensetracker.android.util.getPlaceHolderRes
import com.ajay.seenu.expensetracker.android.util.getStringRes
import com.ajay.seenu.expensetracker.domain.model.Category
import com.ajay.seenu.expensetracker.domain.model.DateFilter
import com.ajay.seenu.expensetracker.domain.model.TransactionType

@Composable
fun ExpenseByCategoryDonutSection(
    modifier: Modifier = Modifier,
    filter: DateFilter = DateFilter.ThisMonth,
    viewModel: SimpleAnalyticsViewModel = hiltViewModel(),
) {
    val selectedType by viewModel.selectedType.collectAsStateWithLifecycle()
    val state by viewModel.data.collectAsStateWithLifecycle()

    LaunchedEffect(filter, selectedType) {
        viewModel.changeType(selectedType, filter)
    }

    var selectedCategory: Category? by rememberSaveable(filter, selectedType) {
        mutableStateOf(null)
    }

    val filterableTypes = remember {
        TransactionType.entries.filter { it != TransactionType.TRANSFER }
    }
    val typeLabels = filterableTypes.map { stringResource(it.getStringRes()) }

    Column(modifier = modifier) {
        SlidingSwitch(
            selectedValue = stringResource(selectedType.getStringRes()),
            values = typeLabels,
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp),
            shape = RoundedCornerShape(12.dp),
        ) { index, _ ->
            viewModel.changeType(filterableTypes[index], filter)
        }
        Spacer(modifier = Modifier.height(12.dp))

        when (val uiState = state) {
            UiState.Loading, UiState.Empty -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            is UiState.Failure -> {
                InsufficientDataCard(
                    Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                )
            }

            is UiState.Success -> {
                val data = uiState.data
                val chartData = data.chartData
                val legendData = data.legendData

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                ) {
                    val totalLabel = chartData.sum.asCurrency(LocalCurrencySymbol.current)
                    val style = PieChartStyle(
                        strokeWidth = 28.dp,
                        highlightStrokeWidth = 16.dp,
                        textStyle = TextStyle(
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    val selectedEntry = chartData.entries.find { it.extras == selectedCategory }
                    PieChart(
                        data = chartData,
                        selectedEntry = selectedEntry,
                        label = totalLabel,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .padding(4.dp),
                        style = style,
                    ) { event ->
                        selectedCategory = if (event is PieChartEvent.Selected) {
                            event.entry.extras as Category
                        } else {
                            null
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                DonutLegend(
                    legendData = legendData,
                    selectedCategory = selectedCategory,
                    onTapped = { selectedCategory = it },
                )
            }
        }
    }
}

@Composable
private fun DonutLegend(
    legendData: List<AnalyticsLegendRowData>,
    selectedCategory: Category?,
    onTapped: (Category?) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        legendData.forEach { row ->
            val isSelected = selectedCategory != null && row.category == selectedCategory
            DonutLegendRow(
                data = row,
                isSelected = isSelected,
                modifier = Modifier.clickable {
                    if (selectedCategory == row.category) onTapped(null) else onTapped(row.category)
                },
            )
        }
    }
}

@Composable
private fun DonutLegendRow(
    data: AnalyticsLegendRowData,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(6.dp)
    val background = if (isSelected) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(background, shape)
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(10.dp).background(data.color, shape))
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = data.label,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp,
            )
            val amount = stringResource(
                data.category.type.getPlaceHolderRes(),
                data.amount.asCurrency(LocalCurrencySymbol.current),
            )
            Text(
                text = amount,
                style = TextStyle(
                    color = data.category.type.getColor(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
            )
        }
        Spacer(modifier = Modifier.size(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, shape)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(data.percentage / 100f)
                    .height(4.dp)
                    .background(data.color, shape)
            )
        }
    }
}
