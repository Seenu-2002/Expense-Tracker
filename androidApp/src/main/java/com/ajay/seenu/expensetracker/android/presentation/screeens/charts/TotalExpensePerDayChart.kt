package com.ajay.seenu.expensetracker.android.presentation.screeens.charts

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ajay.seenu.expensetracker.android.domain.data.Filter
import com.ajay.seenu.expensetracker.android.presentation.screeens.ChartDefaults.color4
import com.ajay.seenu.expensetracker.android.presentation.screeens.ChartDefaults.columnChartColors
import com.ajay.seenu.expensetracker.android.presentation.screeens.Loader
import com.ajay.seenu.expensetracker.android.presentation.viewmodels.chart_viewmodels.TotalExpensePerDayChartViewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineSpec
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.ProvideVicoTheme
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.shader.color
import com.patrykandpatrick.vico.compose.m3.common.rememberM3VicoTheme
import com.patrykandpatrick.vico.core.cartesian.axis.AxisPosition
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.shader.DynamicShader
import com.patrykandpatrick.vico.core.common.shape.Shape

@Composable
fun TotalExpensePerDayChart(
    modifier: Modifier = Modifier,
    filter: Filter = Filter.All,
    viewModel: TotalExpensePerDayChartViewModel = hiltViewModel(),
) {
    val isChartLoadingCompleted = viewModel.isChartLoadingCompleted.collectAsState()

    viewModel.setFilter(filter)

    if (!isChartLoadingCompleted.value) {
        return Loader(modifier)
    }

    val bottomAxis = rememberBottomAxis(
        guideline = null,
        valueFormatter = CartesianValueFormatter { value, chartValues, _ ->
            chartValues.model.extraStore[viewModel.labelListKey][value.toInt()]
        }
    )
    ProvideVicoTheme(rememberM3VicoTheme(textColor = Color.Red)) {
        CartesianChartHost(
            chart =
            rememberCartesianChart(
                rememberColumnCartesianLayer(
                    columnProvider =
                    ColumnCartesianLayer.ColumnProvider.series(
                        columnChartColors.map { color ->
                            rememberLineComponent(
                                color = color,
                                thickness = 38.dp,
                                shape = Shape.rounded(2)
                            )
                        }
                    ),
                    mergeMode = { ColumnCartesianLayer.MergeMode.Stacked },
                    verticalAxisPosition = AxisPosition.Vertical.Start,
                ),
                rememberLineCartesianLayer(
                    lines = listOf(rememberLineSpec(shader = DynamicShader.color(color4))),
                    verticalAxisPosition = AxisPosition.Vertical.End,
                ),
                startAxis = rememberStartAxis(),
                bottomAxis = bottomAxis,
            ),
            modelProducer = viewModel.modelProducer,
            modifier = modifier,
            runInitialAnimation = true
        )
    }

}