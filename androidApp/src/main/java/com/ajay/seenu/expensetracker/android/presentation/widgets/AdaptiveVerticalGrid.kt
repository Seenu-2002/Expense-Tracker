package com.ajay.seenu.expensetracker.android.presentation.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Preview
@Composable
private fun AdaptiveVerticalGridPreview() {
    AdaptiveVerticalGrid(
        adaptiveCellSize = 48.dp,
    ) {
        repeat(20) {
            Box(
                modifier = Modifier.background(
                    color = Color.LightGray,
                    shape = RoundedCornerShape(24.dp)
                ), contentAlignment = Alignment.Center
            ) {
                Text(text = "${it + 1}")
            }
        }
    }
}

@Composable
fun AdaptiveVerticalGrid(
    adaptiveCellSize: Dp,
    gap: Dp = 0.dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {}
) {
    Layout(modifier = modifier, content = content) { measurables, constraints ->

        val adaptiveCellSizeInPx = adaptiveCellSize.toPx()
        val adaptiveCellSizeInPxInt = adaptiveCellSize.toPx().roundToInt()
        val gapInPx = gap.toPx()
        val perItemWidth = adaptiveCellSizeInPx + (gapInPx * 2)
        val maxItemsPerRow = (constraints.maxWidth / perItemWidth)
        val remainingSpace = constraints.maxWidth - (maxItemsPerRow * perItemWidth)
        val adjustedGap: Float = if (remainingSpace > 0) {
            gapInPx + (remainingSpace / (maxItemsPerRow * 2))
        } else {
            gapInPx
        }

        val placeables = mutableListOf<Placeable>()
        val childConstraints = constraints.copy(
            minWidth = adaptiveCellSizeInPxInt,
            maxWidth = adaptiveCellSizeInPxInt,
            minHeight = adaptiveCellSizeInPxInt,
            maxHeight = adaptiveCellSizeInPxInt
        )
        for (measurable in measurables) {
            placeables.add(
                measurable.measure(childConstraints)
            )
        }
        val rowCount = (placeables.size + maxItemsPerRow - 1) / maxItemsPerRow

        layout(constraints.maxWidth, (rowCount * (perItemWidth + (adjustedGap))).roundToInt()) {
            var x = adjustedGap
            var y = adjustedGap
            for (placeable in placeables) {
                placeable.placeRelative(x.roundToInt(), y.roundToInt())
                x += placeable.width + (adjustedGap * 2)
                if (x + placeable.width > constraints.maxWidth) {
                    x = adjustedGap
                    y += placeable.height + (adjustedGap * 2)
                }
            }
        }
    }
}