package com.ajay.seenu.expensetracker.android.presentation.widgets

import androidx.annotation.FloatRange
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Preview(showBackground = true)
@Composable
fun DailySpendBarChart(modifier: Modifier = Modifier, date: Date = Date(), values: List<Int> = arrayListOf(1244, 1212, 111, 4444, 1212, 1111, 3434)) {

    val dates = arrayListOf<Date>()
    val calendar = Calendar.getInstance()
    calendar.time = date

    dates.add(date)
    repeat(6) {
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        dates.add(0, calendar.time)
    }
    val maxY = values.maxOrNull() ?: 0
    val barWidth = 24.dp

    val uiFormatter = SimpleDateFormat("dd MMM", Locale.ENGLISH)

    val textMeasurer = rememberTextMeasurer()
    val textStyle = TextStyle(
        fontSize = 12.sp,
        color = Color.White
    )

    fun DrawScope.drawBar(@FloatRange(0.0, 1.0) percentage: Float, bottomY: Float, midX: Float) {
        val startX = midX - (barWidth / 2).toPx()
        val topY = bottomY * percentage
        val size = Size(barWidth.toPx(), bottomY - topY)
        drawRoundRect(Color.Red, topLeft = Offset(startX, topY), size = size, cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx()))
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val padding = 8.dp.toPx()
        val widthWithPadding = width - (padding * 2)

        drawCircle(Color.Green, width / 2F)

        var startX = padding
        val texts = arrayListOf<Pair<String, Int>>()
        var overallTextWidth = 0F
        dates.forEach {
            val text = uiFormatter.format(it)
            val textWidth = textMeasurer.measure(text, textStyle).size.width
            overallTextWidth += textWidth
            texts.add(text to textWidth)
        }
        val distanceBetweenTwoDates = ((widthWithPadding - overallTextWidth) / (dates.size - 1))
        texts.forEachIndexed { index, it ->
            drawText(textMeasurer = textMeasurer, text = it.first, style = textStyle, topLeft = Offset(startX, height - 12.sp.toPx() - padding))
            val value = values[index]
            val percentage = 1F - (value / maxY.toFloat())
            drawBar(percentage, height - 12.sp.toPx() - padding, startX + (it.second / 2F))
            startX += distanceBetweenTwoDates + it.second
        }
    }
}