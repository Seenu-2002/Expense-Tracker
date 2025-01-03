package com.ajay.seenu.expensetracker.android.presentation.screeens.charts

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.atan2

@Composable
fun PieChart(
    data: PieChartData,
    label: String,
    style: PieChartStyle = PieChartStyle(),
    selectedEntry: Entry? = null,
    modifier: Modifier = Modifier,
    onEvent: ((event: PieChartEvent) -> Unit)? = null
) {

    var _selectedShape: ArcShape? by remember {
        mutableStateOf(null)
    }
    val shapes = remember(data) {
        val shapeList = mutableStateListOf<ArcShape>()
        var startAngle = 0F
        data.entries.forEach { entry ->
            val sweep = ((entry.y / data.sum) * 360).toFloat() // (360 / 100)
            val shape = ArcShape(
                startAngle,
                startAngle + sweep,
                entry
            )
            startAngle += sweep
            shapeList.add(shape)

            if (entry == selectedEntry) {
                _selectedShape = shape
            }
        }
        shapeList
    }
    val textMeasurer = rememberTextMeasurer()

    LaunchedEffect(data, selectedEntry) {
        _selectedShape = if (selectedEntry == null) {
            null
        } else {
            shapes.find { it.data == selectedEntry }
        }
    }
    var center by remember {
        mutableStateOf(Offset(0F, 0F))
    }

    var animationTarget by remember {
        mutableFloatStateOf(0F)
    }

    val animationProgress: Float by animateFloatAsState(
        targetValue = animationTarget,
        animationSpec = tween(750),
        label = "Pie Chart Animation"
    )

    val progress = if (LocalInspectionMode.current) {
        1F
    } else {
        animationProgress - animationTarget + 1
    }

    LaunchedEffect(data) {
        animationTarget += 1
    }

    Canvas(modifier.pointerInput(data) {
        detectTapGestures(onTap = {
            val angle =
                ((atan2(it.y - center.y, it.x - center.x) * 180) / kotlin.math.PI).let { angle ->
                    if (angle < 0) {
                        angle + 360
                    } else {
                        angle
                    }
                }
            for (shape in shapes) {
                if (shape has angle) {
                    if (_selectedShape == shape) {
                        _selectedShape = null
                        onEvent?.invoke(PieChartEvent.DeSelected(shape.data))
                    } else {
                        _selectedShape = shape
                        onEvent?.invoke(PieChartEvent.Selected(shape.data))
                    }
                    break
                }
            }
        })
    }) {
        center = size.center
        val highlightStrokeWidthPx = style.highlightStrokeWidth.toPx()
        val strokeWidthPx = style.strokeWidth.toPx()
        val outerRadius = size.minDimension
        val innerRadius = outerRadius - highlightStrokeWidthPx - strokeWidthPx
        val outerTopLeft = Offset(
            ((size.width - size.minDimension) / 2) + (highlightStrokeWidthPx / 2),
            ((size.height - size.minDimension) / 2) + (highlightStrokeWidthPx / 2)
        )
        val innerTopLeft = Offset(
            outerTopLeft.x + (strokeWidthPx / 2) + (highlightStrokeWidthPx / 2),
            outerTopLeft.y + (strokeWidthPx / 2) + (highlightStrokeWidthPx / 2)
        )
        val outerSize = Size(
            outerRadius - highlightStrokeWidthPx,
            outerRadius - highlightStrokeWidthPx
        )
        val innerSize = Size(
            innerRadius - highlightStrokeWidthPx,
            innerRadius - highlightStrokeWidthPx
        )
        var startAngle = 0F
        for (shape in shapes) {
            val sweep = shape.sweep * progress
            drawArc(
                shape.data.color, startAngle, sweep, false, style = Stroke(
                    strokeWidthPx,
                ),
                topLeft = innerTopLeft,
                size = innerSize
            )
            if (_selectedShape == shape) {
                drawArc(
                    shape.data.color.copy(alpha = .5F),
                    startAngle,
                    sweep,
                    false,
                    style = Stroke(
                        highlightStrokeWidthPx,
                    ),
                    topLeft = outerTopLeft,
                    size = outerSize
                )
            }

            startAngle += sweep
        }

        val textLayoutResult = textMeasurer.measure(label, style = style.textStyle)
        val textOffset = Offset(
            (size.width - textLayoutResult.size.width) / 2,
            (size.height - textLayoutResult.size.height) / 2
        )
        drawText(textLayoutResult, style.textStyle.color, textOffset)
    }
}

@Immutable
data class PieChartStyle(
    val strokeWidth: Dp = 16.dp,
    val highlightStrokeWidth: Dp = 12.dp,
    val textStyle: TextStyle = TextStyle()
)

@Immutable
data class Entry(
    val x: String,
    val y: Double,
    val color: Color,
    val extras: Any? = null
)

data class PieChartData(
    val entries: List<Entry>
) {

    val sum = entries.sumOf { it.y }

}

sealed interface PieChartEvent {
    data class Selected(val entry: Entry) : PieChartEvent
    data class DeSelected(val entry: Entry) : PieChartEvent
}

@Preview
@Composable
private fun PieChartDataPreview() {
    val data = (0..10).map {
        Entry(
            "Entry $it",
            (100..1000).random().toDouble(),
            Color((Math.random() * 16777215).toInt() or (0xFF shl 24))
        )
    }
    val style = PieChartStyle(
        textStyle = TextStyle(
            color = Color.Green,
            fontStyle = FontStyle.Italic
        )
    )
    PieChart(
        PieChartData(data), modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        label = "$ 10,00,00,00,000.00",
        style = style,
        selectedEntry = data.random()
    )
}

@Immutable
private data class ArcShape(
    val fromAngle: Float,
    val toAngle: Float,
    val data: Entry
) {

    val sweep: Float
        get() = toAngle - fromAngle

    infix fun has(angle: Double): Boolean {
        return angle in fromAngle..toAngle
    }

}