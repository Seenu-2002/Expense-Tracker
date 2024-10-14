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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ajay.seenu.expensetracker.android.R
import com.ajay.seenu.expensetracker.android.data.FilterPreference
import com.ajay.seenu.expensetracker.android.domain.data.Filter
import com.ajay.seenu.expensetracker.android.presentation.screeens.charts.ExpenseByCategoryChart
import com.ajay.seenu.expensetracker.android.presentation.screeens.charts.ExpenseByPaymentTypeChart
import com.ajay.seenu.expensetracker.android.presentation.screeens.charts.TotalExpensePerDayChart
import com.ajay.seenu.expensetracker.android.presentation.viewmodels.AnalyticsViewModel
import com.ajay.seenu.expensetracker.android.presentation.viewmodels.Charts
import com.ajay.seenu.expensetracker.android.presentation.widgets.DateRangePickerBottomSheet
import com.ajay.seenu.expensetracker.android.presentation.widgets.FilterBottomSheet
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(navController: NavController, viewModel: AnalyticsViewModel = hiltViewModel()) {

    val currentFilter by viewModel.currentFilter.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState()
    var openFilterBottomSheet by rememberSaveable {
        mutableStateOf(false)
    }
    val dateRangePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = (currentFilter as? Filter.Custom)?.startDate,
        initialSelectedEndDateMillis = (currentFilter as? Filter.Custom)?.endDate
    )
    val dateRangeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var openDateRangePicker by rememberSaveable {
        mutableStateOf(false)
    }
    val formatter = remember {
        SimpleDateFormat(
            "dd MMM, yyyy",
            Locale.ENGLISH
        ) // TODO: User configured date format
    }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val filter = FilterPreference.getCurrentFilter(context)
        viewModel.setFilter(context, filter)
    }

    LaunchedEffect(currentFilter) {
        val filter = currentFilter
        if (filter is Filter.Custom) {
            dateRangePickerState.setSelection(filter.startDate, filter.endDate)
        }
    }

    Scaffold(topBar = {
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
                    if (currentFilter != Filter.ThisMonth) {
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
                .padding(paddingValues)
        ) {
            items(viewModel.chartOrder.size) {
                when (val chartType = viewModel.chartOrder[it]) {
                    Charts.TOTAL_EXPENSE_PER_DAY_BY_CATEGORY -> {
                        ChartContainer(Modifier, chartType.label) {
                            TotalExpensePerDayChart(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                                    .height(300.dp),
                                currentFilter
                            )
                        }
                    }

                    Charts.EXPENSE_BY_CATEGORY -> {
                        ChartContainer(Modifier, chartType.label) {
                            ExpenseByCategoryChart(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                                    .height(300.dp),
                                currentFilter
                            )
                        }
                    }

                    Charts.EXPENSE_BY_PAYMENT_TYPE -> {
                        ChartContainer(Modifier, chartType.label) {
                            ExpenseByPaymentTypeChart(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                                    .height(300.dp),
                                currentFilter
                            )
                        }
                    }
                }
            }
        }

        if (openFilterBottomSheet) {
            FilterBottomSheet(
                sheetState = sheetState,
                filter = currentFilter,
                formatter = formatter,
                onFilterSelected = { filter ->
                    when (filter) {
                        Filter.CUSTOM_MOCK -> {
                            openDateRangePicker = true
                            openFilterBottomSheet = false
                        }

                        else -> {
                            openFilterBottomSheet = false
                            viewModel.setFilter(context, filter)
                        }
                    }
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
                formatter = formatter,
                onDateSelected = { startDate, endDate ->
                    openDateRangePicker = false
                    viewModel.setFilter(context, Filter.Custom(startDate, endDate))
                })
        }
    }
}

@Composable
fun ChartContainer(modifier: Modifier = Modifier, title: String, chart: @Composable () -> Unit) {
    Column(modifier = modifier) {
        Text(title)
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
        )
        chart()
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

object ChartDefaults {
    val color1 = Color(0xffa55a5a)
    val color2 = Color(0xffd3756b)
    val color3 = Color(0xfff09b7d)
    val color4 = Color(0xffffc3a1)
    val columnChartColors = listOf(color1, color2, color3, color4)
}