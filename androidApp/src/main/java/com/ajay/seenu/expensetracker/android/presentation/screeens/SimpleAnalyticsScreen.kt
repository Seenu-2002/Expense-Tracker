package com.ajay.seenu.expensetracker.android.presentation.screeens

import android.app.Activity
import androidx.annotation.FloatRange
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ajay.seenu.expensetracker.android.R
import com.ajay.seenu.expensetracker.android.presentation.common.ChartDefaults
import com.ajay.seenu.expensetracker.android.presentation.components.SlidingSwitch
import com.ajay.seenu.expensetracker.android.presentation.screeens.charts.PieChart
import com.ajay.seenu.expensetracker.android.presentation.screeens.charts.PieChartData
import com.ajay.seenu.expensetracker.android.presentation.screeens.charts.PieChartEvent
import com.ajay.seenu.expensetracker.android.presentation.screeens.charts.PieChartStyle
import com.ajay.seenu.expensetracker.android.presentation.state.Error
import com.ajay.seenu.expensetracker.android.presentation.state.UiState
import com.ajay.seenu.expensetracker.android.presentation.viewmodels.SimpleAnalyticsViewModel
import com.ajay.seenu.expensetracker.android.util.asCurrency
import com.ajay.seenu.expensetracker.android.util.getColor
import com.ajay.seenu.expensetracker.android.util.getPlaceHolderRes
import com.ajay.seenu.expensetracker.android.util.getStringRes
import com.ajay.seenu.expensetracker.domain.model.Category
import com.ajay.seenu.expensetracker.domain.model.TransactionType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.random.Random

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class,
    ExperimentalSerializationApi::class
)
@Composable
fun SimpleAnalyticsScreen(
    viewmodel: SimpleAnalyticsViewModel = hiltViewModel()
) {

    val selectedType by viewmodel.selectedType.collectAsStateWithLifecycle()
    val state by viewmodel.data.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewmodel.changeType(TransactionType.EXPENSE)
    }

    var selectedCategory: Category? by rememberSaveable(state /*saver*/) { // TODO: Custom Saver Implementation
        mutableStateOf(null)
    }

    val activity = LocalContext.current as Activity
    val windowSize = calculateWindowSizeClass(activity)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text(
                    text = stringResource(R.string.analytics), style = TextStyle(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            })
        }
    ) { paddingValues ->

        when (val uiState = state) {
            UiState.Loading, UiState.Empty -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is UiState.Failure -> {
                when (uiState.error) {
                    Error.Empty -> {
                        EmptyDataErrorViewForAnalytics(modifier = Modifier.fillMaxSize())
                    }

                    else -> {
                        return@Scaffold // FIXME
                    }
                }
            }

            is UiState.Success -> {
                val data = uiState.data
                val chartData = data.chartData
                val legendData = data.legendData

                if (windowSize.widthSizeClass <= WindowWidthSizeClass.Medium) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        PieChartContainer(
                            chartData,
                            selectedCategory, modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = paddingValues.calculateTopPadding())
                                .fillMaxHeight(.4F)
                        ) { event ->
                            selectedCategory = if (event is PieChartEvent.Selected) {
                                event.entry.extras as Category
                            } else {
                                null
                            }
                        }
                        PieChartLegendContainer(
                            legendData,
                            selectedCategory,
                            selectedType,
                            modifier = Modifier
                                .weight(1F),
                            onTypeChanged = { type ->
                                viewmodel.changeType(type)
                            },
                            onTapped = {
                                selectedCategory = it
                            }
                        )
                    }
                } else {
                    Row(modifier = Modifier.fillMaxSize()) {
                        PieChartContainer(
                            chartData,
                            selectedCategory, modifier = Modifier
                                .fillMaxHeight()
                                .padding(top = paddingValues.calculateTopPadding())
                                .weight(1F)
                        ) { event ->
                            selectedCategory = if (event is PieChartEvent.Selected) {
                                event.entry.extras as Category
                            } else {
                                null
                            }
                        }
                        PieChartLegendContainer(
                            legendData,
                            selectedCategory,
                            selectedType,
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(top = paddingValues.calculateTopPadding())
                                .padding(top = 12.dp)
                                .weight(1F),
                            onTypeChanged = { type ->
                                viewmodel.changeType(type)
                            },
                            onTapped = {
                                selectedCategory = it
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PieChartContainer(
    chartData: PieChartData,
    selectedCategory: Category?,
    modifier: Modifier = Modifier,
    onChartEvent: (event: PieChartEvent) -> Unit
) {
    Box(modifier = modifier) {
        val totalLabel = chartData.sum.asCurrency()
        val style = PieChartStyle(
            strokeWidth = 28.dp,
            highlightStrokeWidth = 16.dp,
            textStyle = TextStyle(
                fontSize = 22.sp,
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
                .matchParentSize()
                .padding(4.dp),
            style = style
        ) {
            onChartEvent(it)
        }
    }
}

@Composable
fun PieChartLegendContainer(
    legendData: List<AnalyticsLegendRowData>,
    selectedCategory: Category?,
    selectedType: TransactionType,
    modifier: Modifier = Modifier,
    onTypeChanged: (TransactionType) -> Unit,
    onTapped: (Category?) -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val values = TransactionType.entries.map { stringResource(it.getStringRes()) }
        SlidingSwitch(
            selectedValue = stringResource(selectedType.getStringRes()),
            values = values,
            modifier = Modifier.widthIn(max = 600.dp),
            shape = RoundedCornerShape(12.dp)
        ) { index, _ ->
            val type = TransactionType.entries[index]
            onTypeChanged(type)
        }
        val listState = rememberLazyListState()
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F)
                .padding(vertical = 8.dp)
        ) {
            items(legendData.size, key = {
                it
            }) {

                val style = TextStyle(
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                val data = legendData[it]
                val isSelected = selectedCategory != null && data.category == selectedCategory

                val rowModifier = Modifier.clickable {
                    if (selectedCategory?.label == data.label) {
                        onTapped(null)
                    } else {
                        onTapped(data.category)
                    }
                }
                AnalyticsLegendRow(
                    data = data,
                    isSelected = isSelected,
                    labelStyle = style,
                    modifier = rowModifier
                )
            }
        }

        LaunchedEffect(legendData) {
            listState.scrollToItem(0)
        }
    }
}

@Composable
fun AnalyticsLegendRow(
    data: AnalyticsLegendRowData,
    isSelected: Boolean = false,
    modifier: Modifier = Modifier,
    labelStyle: TextStyle = TextStyle.Default,
) {

    val shape = RoundedCornerShape(6.dp)

    Column(modifier.let {
        if (isSelected) {
            modifier.background(Color.DarkGray, shape)
        } else {
            modifier
        }
    }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(
                modifier = Modifier
                    .size(12.dp)
                    .background(data.color, shape)
            )

            val amount = stringResource(data.category.type.getPlaceHolderRes(), data.amount.asCurrency())
            val color = data.category.type.getColor()

            Text(
                text = data.label,
                modifier = Modifier
                    .weight(1F)
                    .padding(horizontal = 6.dp),
                style = labelStyle
            )
            Text(
                text = amount,
                style = TextStyle(color = color, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 12.dp)
        ) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(Color.LightGray, shape)
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth(data.percentage / 100)
                    .height(6.dp)
                    .background(data.color, shape)
            )
        }
    }
}

@Preview
@Composable
private fun AnalyticsLegendRowDataPreview() {
    val data = AnalyticsLegendRowData(
        "Random",
        (10..100000).random().toDouble(),
        ChartDefaults.getDynamicColor((0..100).random()),
        (0..100).random().toFloat(),
        Category(
            212L,
            TransactionType.entries.random(),
            "Random",
            Color.Red.toArgb().toLong(),
            R.drawable.monetization_on
        )
    )
    AnalyticsLegendRow(data = data, isSelected = Random.nextBoolean())
}

data class AnalyticsLegendRowData constructor(
    val label: String,
    val amount: Double,
    val color: Color,
    @FloatRange(0.0, 100.0)
    val percentage: Float,
    val category: Category
)

data class AnalyticsScreenData(
    val chartData: PieChartData,
    val legendData: List<AnalyticsLegendRowData>
)

@Preview
@Composable
fun EmptyDataErrorViewForAnalytics(modifier: Modifier = Modifier) {
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier
                .padding(8.dp)
                .size(60.dp),
            painter = painterResource(R.drawable.icon_filter_list),
            contentDescription = "Error Icon"
        )
        Text(
            text = stringResource(R.string.error_empty_data_on_analytics_title),
            modifier = Modifier.padding(horizontal = 8.dp),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        Text(
            text = stringResource(R.string.error_empty_data_on_analytics_message),
            modifier = Modifier.padding(8.dp),
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
        )
    }
}