package com.ajay.seenu.expensetracker.android.presentation.screeens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ajay.seenu.expensetracker.android.presentation.screeens.charts.ExpenseByCategoryChart
import com.ajay.seenu.expensetracker.android.presentation.screeens.charts.ExpenseByPaymentTypeChart
import com.ajay.seenu.expensetracker.android.presentation.screeens.charts.TotalExpensePerDayChart
import com.ajay.seenu.expensetracker.android.presentation.viewmodels.AnalyticsViewModel
import com.ajay.seenu.expensetracker.android.presentation.viewmodels.Charts

@Composable
fun AnalyticsScreen(navController: NavController, viewModel: AnalyticsViewModel = hiltViewModel()) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 2.dp)
    ) {
        items(viewModel.chartOrder.size) {
            when (val chartType = viewModel.chartOrder[it]) {
                Charts.TOTAL_EXPENSE_PER_DAY_BY_CATEGORY -> {
                    ChartContainer(Modifier, chartType.label) {
                        TotalExpensePerDayChart(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                .height(300.dp)
                        )
                    }
                }

                Charts.EXPENSE_BY_CATEGORY -> {
                    ChartContainer(Modifier, chartType.label) {
                        ExpenseByCategoryChart(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                .height(300.dp)
                        )
                    }
                }

                Charts.EXPENSE_BY_PAYMENT_TYPE -> {
                    ChartContainer(Modifier, chartType.label) {
                        ExpenseByPaymentTypeChart(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                .height(300.dp)
                        )
                    }
                }
            }
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

object ChartDefaults {
    val color1 = Color(0xffa55a5a)
    val color2 = Color(0xffd3756b)
    val color3 = Color(0xfff09b7d)
    val color4 = Color(0xffffc3a1)
    val columnChartColors = listOf(color1, color2, color3, color4)
}