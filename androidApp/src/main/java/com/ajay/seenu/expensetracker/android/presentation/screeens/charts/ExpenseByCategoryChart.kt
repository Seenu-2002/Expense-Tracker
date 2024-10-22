package com.ajay.seenu.expensetracker.android.presentation.screeens.charts

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ajay.seenu.expensetracker.android.R
import com.ajay.seenu.expensetracker.android.domain.data.Filter
import com.ajay.seenu.expensetracker.android.presentation.common.ChartDefaults
import com.ajay.seenu.expensetracker.android.presentation.common.rememberMarker
import com.ajay.seenu.expensetracker.android.presentation.screeens.InsufficientDataCard
import com.ajay.seenu.expensetracker.android.presentation.screeens.Loader
import com.ajay.seenu.expensetracker.android.presentation.viewmodels.chart_viewmodels.ExpenseByCategoryChartViewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.ProvideVicoTheme
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.m3.common.rememberM3VicoTheme
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer

@Composable
fun ExpenseByCategoryChart(
    modifier: Modifier = Modifier,
    filter: Filter = Filter.ThisMonth,
    viewModel: ExpenseByCategoryChartViewModel = hiltViewModel(),
) {
    val chartState = viewModel.chartState.collectAsState()

    viewModel.setFilter(filter)
    when (chartState.value) {
        ChartState.Fetching, ChartState.Empty -> {
            return Loader(modifier)
        }

        is ChartState.Failed -> {
            return InsufficientDataCard(
                Modifier
                    .fillMaxWidth()
                    .height(200.dp))
        }

        else -> {
            // Nothing has to be done
        }
    }

    ProvideVicoTheme(rememberM3VicoTheme(textColor = MaterialTheme.colorScheme.onPrimaryContainer)) {
        val titleTextComponent = rememberTextComponent(color = MaterialTheme.colorScheme.onPrimaryContainer)
        val startAxis = rememberStartAxis(title = stringResource(R.string.chart_axis_amount), titleComponent = titleTextComponent)
        val bottomAxis =
            rememberBottomAxis(
                title = stringResource(R.string.chart_axis_category),
                titleComponent = titleTextComponent,
                valueFormatter = CartesianValueFormatter { value, chartValues, _ ->
                    chartValues.model.extraStore[viewModel.labelListKey][value.toInt()]
                })
        val layer = rememberColumnCartesianLayer(
            columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                rememberLineComponent(
                    color = ChartDefaults.expenseColor,
                    thickness = 32.dp
                )
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
            modelProducer = viewModel.modelProducer,
            marker = rememberMarker()
        )
    }

}