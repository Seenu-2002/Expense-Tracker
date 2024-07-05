package com.ajay.seenu.expensetracker.android.presentation.screeens.charts

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ajay.seenu.expensetracker.android.presentation.screeens.ChartDefaults.columnChartColors
import com.ajay.seenu.expensetracker.android.presentation.viewmodels.chart_viewmodels.ExpenseByPaymentTypeChartViewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.ProvideVicoTheme
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.m3.common.rememberM3VicoTheme
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer

@Composable
internal fun ExpenseByPaymentTypeChart(
    modifier: Modifier = Modifier,
    viewModel: ExpenseByPaymentTypeChartViewModel = hiltViewModel<ExpenseByPaymentTypeChartViewModel>(),
) {

    LaunchedEffect(Unit) {
        viewModel.getData()
    }

    ProvideVicoTheme(rememberM3VicoTheme(textColor = Color.Red)) {
        val startAxis = rememberStartAxis()
        val bottomAxis = rememberBottomAxis(valueFormatter = CartesianValueFormatter { value, chartValues, _ ->
            chartValues.model.extraStore[viewModel.labelListKey][value.toInt()]
        })
        val layer = rememberColumnCartesianLayer(
            columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                columnChartColors.map { color ->
                    rememberLineComponent(color = color,
                        thickness = 32.dp)
                }
            )
        )
        val chart = rememberCartesianChart(
            layer,
            startAxis = startAxis,
            bottomAxis = bottomAxis,
        )
        CartesianChartHost(
            modifier = modifier,
            chart = chart,
            modelProducer = viewModel.modelProducer
        )
    }
}