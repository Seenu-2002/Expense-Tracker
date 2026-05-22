package com.ajay.seenu.expensetracker.android.presentation.screeens.charts

import android.graphics.Typeface
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ajay.seenu.expensetracker.android.presentation.common.ChartDefaults
import com.ajay.seenu.expensetracker.android.presentation.common.rememberMarker
import com.ajay.seenu.expensetracker.android.presentation.screeens.InsufficientDataCard
import com.ajay.seenu.expensetracker.android.presentation.screeens.Loader
import com.ajay.seenu.expensetracker.android.presentation.viewmodels.chart_viewmodels.IncomeExpenseTrendViewModel
import com.ajay.seenu.expensetracker.domain.model.DateFilter
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineSpec
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.ProvideVicoTheme
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.of
import com.patrykandpatrick.vico.compose.common.rememberHorizontalLegend
import com.patrykandpatrick.vico.compose.common.rememberLegendItem
import com.patrykandpatrick.vico.compose.common.shader.color
import com.patrykandpatrick.vico.compose.m3.common.rememberM3VicoTheme
import com.patrykandpatrick.vico.core.cartesian.CartesianDrawContext
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasureContext
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.shader.DynamicShader
import com.patrykandpatrick.vico.core.common.shape.Shape

@Composable
fun IncomeExpenseTrendChart(
    modifier: Modifier = Modifier,
    filter: DateFilter = DateFilter.ThisMonth,
    viewModel: IncomeExpenseTrendViewModel = hiltViewModel(),
) {
    val state by viewModel.chartState.collectAsStateWithLifecycle()

    LaunchedEffect(filter) { viewModel.setFilter(filter) }

    when (state) {
        ChartState.Fetching, ChartState.Empty -> {
            return Loader(modifier)
        }
        is ChartState.Failed -> {
            return InsufficientDataCard(
                Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )
        }
        else -> Unit
    }

    val titleTextComponent = rememberTextComponent(color = MaterialTheme.colorScheme.onPrimaryContainer)
    val labelTextComponent = rememberTextComponent(color = MaterialTheme.colorScheme.onPrimaryContainer)

    val bottomAxis = rememberBottomAxis(
        guideline = null,
        title = "Date",
        titleComponent = titleTextComponent,
        label = labelTextComponent,
        labelRotationDegrees = 45f,
        valueFormatter = CartesianValueFormatter { value, chartValues, _ ->
            val labels = chartValues.model.extraStore[viewModel.xLabelsKey]
            val idx = value.toInt().coerceIn(0, labels.lastIndex)
            labels[idx]
        }
    )

    val incomeColor = ChartDefaults.incomeColor
    val expenseColor = ChartDefaults.expenseColor

    val incomeLine = rememberLineSpec(shader = DynamicShader.color(incomeColor))
    val expenseLine = rememberLineSpec(shader = DynamicShader.color(expenseColor))

    val legend = rememberHorizontalLegend<CartesianMeasureContext, CartesianDrawContext>(
        items = listOf(
            rememberLegendItem(
                icon = rememberShapeComponent(Shape.Pill, color = incomeColor),
                label = rememberTextComponent(
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textSize = 12.sp,
                    typeface = Typeface.MONOSPACE,
                ),
                labelText = "Income",
            ),
            rememberLegendItem(
                icon = rememberShapeComponent(Shape.Pill, color = expenseColor),
                label = rememberTextComponent(
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textSize = 12.sp,
                    typeface = Typeface.MONOSPACE,
                ),
                labelText = "Expense",
            ),
        ),
        iconSize = 8.dp,
        iconPadding = 8.dp,
        spacing = 12.dp,
        padding = Dimensions.of(top = 8.dp, end = 8.dp),
    )

    ProvideVicoTheme(rememberM3VicoTheme(textColor = MaterialTheme.colorScheme.onPrimaryContainer)) {
        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberLineCartesianLayer(
                    lines = listOf(incomeLine, expenseLine),
                ),
                startAxis = rememberStartAxis(
                    title = "Amount",
                    titleComponent = titleTextComponent,
                ),
                bottomAxis = bottomAxis,
                legend = legend,
            ),
            modelProducer = viewModel.modelProducer,
            modifier = modifier,
            marker = rememberMarker(),
            runInitialAnimation = true,
        )
    }
}
