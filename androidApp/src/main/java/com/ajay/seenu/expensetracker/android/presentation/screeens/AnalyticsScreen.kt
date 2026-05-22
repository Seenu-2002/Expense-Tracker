package com.ajay.seenu.expensetracker.android.presentation.screeens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ajay.seenu.expensetracker.android.R
import com.ajay.seenu.expensetracker.android.data.FilterPreference
import com.ajay.seenu.expensetracker.android.presentation.screeens.charts.BudgetHealthSection
import com.ajay.seenu.expensetracker.android.presentation.screeens.charts.ExpenseByCategoryDonutSection
import com.ajay.seenu.expensetracker.android.presentation.screeens.charts.IncomeExpenseTrendChart
import com.ajay.seenu.expensetracker.android.presentation.screeens.charts.SummaryKpiSection
import com.ajay.seenu.expensetracker.android.presentation.screeens.charts.TopSpendingCategoriesSection
import com.ajay.seenu.expensetracker.android.presentation.screeens.charts.TotalExpensePerDayChart
import com.ajay.seenu.expensetracker.android.presentation.viewmodels.AnalyticsViewModel
import com.ajay.seenu.expensetracker.android.presentation.viewmodels.Charts
import com.ajay.seenu.expensetracker.android.presentation.components.DateRangePickerBottomSheet
import com.ajay.seenu.expensetracker.android.presentation.components.FilterBottomSheet
import com.ajay.seenu.expensetracker.domain.model.DateFilter
import com.ajay.seenu.expensetracker.domain.model.TransactionFilter
import com.ajay.seenu.expensetracker.util.toLocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(navController: NavController, viewModel: AnalyticsViewModel = hiltViewModel()) {

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val filter = FilterPreference.getCurrentFilter(context)
        viewModel.setFilter(filter)
    }

    val currentFilter by viewModel.currentFilter.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState()
    var openFilterBottomSheet by rememberSaveable {
        mutableStateOf(false)
    }
    val dateRangePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = (currentFilter as? DateFilter.Custom)?.startDateInMillis,
        initialSelectedEndDateMillis = (currentFilter as? DateFilter.Custom)?.endDateInMillis
    )
    val dateRangeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var openDateRangePicker by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(currentFilter) {
        val filter = currentFilter
        if (filter is DateFilter.Custom) {
            dateRangePickerState.setSelection(filter.startDateInMillis, filter.endDateInMillis)
        }
    }

    Scaffold(containerColor = MaterialTheme.colorScheme.surfaceContainerLow, topBar = {
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            val box = createRef()
            BadgedBox(
                modifier = Modifier
                    .constrainAs(box) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                    }
                    .padding(4.dp),
                badge = {
                    if (currentFilter != DateFilter.ThisMonth) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(color = MaterialTheme.colorScheme.errorContainer)
                        )
                    }
                }
            ) {
                Icon(
                    modifier = Modifier.clickable {
                        openFilterBottomSheet = true
                    },
                    painter = painterResource(id = R.drawable.icon_filter_list),
                    contentDescription = "filter"
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
        }
    }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp),
        ) {
            items(viewModel.chartOrder.size) { index ->
                val cellModifier = Modifier.fillMaxWidth()
                when (val chartType = viewModel.chartOrder[index]) {
                    Charts.SUMMARY_KPI -> {
                        ChartContainer(cellModifier, chartType.label) {
                            SummaryKpiSection(
                                modifier = Modifier.fillMaxWidth(),
                                filter = currentFilter,
                            )
                        }
                    }

                    Charts.EXPENSE_BY_CATEGORY_DONUT -> {
                        ChartContainer(cellModifier, chartType.label) {
                            ExpenseByCategoryDonutSection(
                                modifier = Modifier.fillMaxWidth(),
                                filter = currentFilter,
                            )
                        }
                    }

                    Charts.INCOME_EXPENSE_TREND -> {
                        ChartContainer(cellModifier, chartType.label) {
                            IncomeExpenseTrendChart(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(260.dp),
                                filter = currentFilter,
                            )
                        }
                    }

                    Charts.TOP_SPENDING_CATEGORIES -> {
                        ChartContainer(cellModifier, chartType.label) {
                            TopSpendingCategoriesSection(
                                modifier = Modifier.fillMaxWidth(),
                                filter = currentFilter,
                            )
                        }
                    }

                    Charts.BUDGET_HEALTH -> {
                        ChartContainer(cellModifier, chartType.label) {
                            BudgetHealthSection(
                                modifier = Modifier.fillMaxWidth(),
                                filter = currentFilter,
                            )
                        }
                    }

                    Charts.TOTAL_EXPENSE_PER_DAY_BY_CATEGORY -> {
                        ChartContainer(cellModifier, chartType.label) {
                            TotalExpensePerDayChart(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                                    .height(300.dp),
                                currentFilter
                            )
                        }
                    }

                    Charts.EXPENSE_BY_CATEGORY -> Unit
                }
            }
        }

        if (openFilterBottomSheet) {
            FilterBottomSheet(
                sheetState = sheetState,
                filter = TransactionFilter(dateFilter = currentFilter),
                formatter = viewModel.getDateFormatter(),
                onFilterSelected = { transactionFilter ->
                    openFilterBottomSheet = false
                    viewModel.setFilter(transactionFilter.dateFilter)
                },
                onCustomDateRequested = {
                    openDateRangePicker = true
                    openFilterBottomSheet = false
                },
                onDismiss = {
                    openFilterBottomSheet = false
                })
        }

        if (openDateRangePicker) {
            DateRangePickerBottomSheet(
                state = dateRangeSheetState,
                dateRangePickerState = dateRangePickerState,
                onDismiss = {
                    openDateRangePicker = false
                },
                formatter = viewModel.getDateFormatter(),
                onDateSelected = { startDate, endDate ->
                    openDateRangePicker = false
                    val startDate = startDate.toLocalDate()
                    val endDate = endDate.toLocalDate()
                    viewModel.setFilter(DateFilter.Custom(startDate, endDate))
                })
        }
    }
}

@Composable
fun ChartContainer(modifier: Modifier = Modifier, title: String, chart: @Composable () -> Unit) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(12.dp))
            chart()
        }
    }
}

@Composable
fun Loader(modifier: Modifier = Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Preview()
@Composable
fun InsufficientDataCard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            modifier = Modifier.size(80.dp),
            painter = painterResource(id = R.drawable.icon_filter_list),
            contentDescription = "Empty"
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "Such Vacant")
    }
}