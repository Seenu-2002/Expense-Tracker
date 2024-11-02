package com.ajay.seenu.expensetracker.android.presentation.screeens.charts

import android.graphics.Typeface
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ajay.seenu.expensetracker.android.R
import com.ajay.seenu.expensetracker.android.domain.data.Filter
import com.ajay.seenu.expensetracker.android.presentation.common.ChartDefaults
import com.ajay.seenu.expensetracker.android.presentation.common.rememberMarker
import com.ajay.seenu.expensetracker.android.presentation.screeens.InsufficientDataCard
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
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.of
import com.patrykandpatrick.vico.compose.common.rememberHorizontalLegend
import com.patrykandpatrick.vico.compose.common.rememberLegendItem
import com.patrykandpatrick.vico.compose.m3.common.rememberM3VicoTheme
import com.patrykandpatrick.vico.core.cartesian.CartesianDrawContext
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasureContext
import com.patrykandpatrick.vico.core.cartesian.axis.AxisPosition
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.shape.Shape

@Composable
fun TotalExpensePerDayChart(
    modifier: Modifier = Modifier,
    filter: Filter = Filter.ThisMonth,
    viewModel: TotalExpensePerDayChartViewModel = hiltViewModel(),
) {
    val isChartLoadingCompleted by viewModel.chartState.collectAsStateWithLifecycle()
    val dateFormat by viewModel.updatedDateFormat.collectAsStateWithLifecycle()

    LaunchedEffect(filter, dateFormat) {
        viewModel.setFilter(filter)
    }
    when (isChartLoadingCompleted) {
        ChartState.Fetching, ChartState.Empty -> {
            return Loader(modifier)
        }

        is ChartState.Failed -> {
            return InsufficientDataCard(
                Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }

        else -> {
            // Nothing has to be done
        }
    }

    val titleTextComponent =
        rememberTextComponent(color = MaterialTheme.colorScheme.onPrimaryContainer)
    val bottomAxis = rememberBottomAxis(
        guideline = null,
        title = stringResource(R.string.chart_axis_date),
        titleComponent = titleTextComponent,
        label = rememberTextComponent(color = MaterialTheme.colorScheme.onPrimaryContainer),
        labelRotationDegrees = 45F,
        valueFormatter = CartesianValueFormatter { value, chartValues, _ ->
            chartValues.model.extraStore[viewModel.labelListKey][value.toInt()]
        }
    )
    val dataSetLabels by viewModel.dataSetLabels.collectAsState()
    val legend = rememberLegend(dataSetLabels)
    ProvideVicoTheme(rememberM3VicoTheme(textColor = MaterialTheme.colorScheme.onPrimaryContainer)) {
        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberColumnCartesianLayer(
                    columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                        ChartDefaults.dynamicColors.take(dataSetLabels.size).map { color ->
                            rememberLineComponent(
                                color = color,
                                thickness = 24.dp,
                                shape = Shape.rounded(2)
                            )
                        }
                    ),
                    mergeMode = { ColumnCartesianLayer.MergeMode.Stacked },
                    verticalAxisPosition = AxisPosition.Vertical.Start,
                ),
                rememberLineCartesianLayer(
                    lines = listOf(rememberLineSpec()),
                    verticalAxisPosition = AxisPosition.Vertical.End,
                ),
                startAxis = rememberStartAxis(
                    title = stringResource(R.string.chart_axis_amount),
                    titleComponent = titleTextComponent
                ),
                bottomAxis = bottomAxis,
                legend = legend
            ),
            modelProducer = viewModel.modelProducer,
            modifier = modifier,
            marker = rememberMarker(),
            runInitialAnimation = true
        )
    }

}

@Composable
private fun rememberLegend(
    dataSetLabels: List<String>,
    colors: List<Color> = ChartDefaults.dynamicColors,
) =
    rememberHorizontalLegend<CartesianMeasureContext, CartesianDrawContext>(
        items = dataSetLabels.mapIndexed { index, label ->
            rememberLegendItem(
                icon = rememberShapeComponent(Shape.Pill, color = colors[index]),
                label =
                rememberTextComponent(
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textSize = 12.sp,
                    typeface = Typeface.MONOSPACE,
                ),
                labelText = label
            )
        },
        iconSize = 8.dp,
        iconPadding = 8.dp,
        spacing = 4.dp,
        padding = Dimensions.of(top = 8.dp, end = 8.dp),
    )