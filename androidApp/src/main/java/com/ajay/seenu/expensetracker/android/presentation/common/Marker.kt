package com.ajay.seenu.expensetracker.android.presentation.common

import android.graphics.Typeface
import android.text.Layout
import android.text.SpannableStringBuilder
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import com.ajay.seenu.expensetracker.android.util.sumOf
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.common.component.fixed
import com.patrykandpatrick.vico.compose.common.component.rememberLayeredComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.of
import com.patrykandpatrick.vico.compose.common.shape.markerCornered
import com.patrykandpatrick.vico.core.cartesian.CartesianDrawContext
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasureContext
import com.patrykandpatrick.vico.core.cartesian.HorizontalDimensions
import com.patrykandpatrick.vico.core.cartesian.Insets
import com.patrykandpatrick.vico.core.cartesian.marker.CandlestickCartesianLayerMarkerTarget
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.ColumnCartesianLayerMarkerTarget
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarkerValueFormatter
import com.patrykandpatrick.vico.core.cartesian.marker.LineCartesianLayerMarkerTarget
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.copyColor
import com.patrykandpatrick.vico.core.common.half
import com.patrykandpatrick.vico.core.common.shape.Corner
import com.patrykandpatrick.vico.core.common.shape.Shape

@Composable
internal fun rememberMarker(
    labelPosition: DefaultCartesianMarker.LabelPosition = DefaultCartesianMarker.LabelPosition.Top,
    showIndicator: Boolean = true,
): CartesianMarker {
    val labelBackgroundShape = Shape.markerCornered(Corner.FullyRounded)
    val labelBackground =
        rememberShapeComponent(labelBackgroundShape, MaterialTheme.colorScheme.surface)
            .setShadow(
                radius = LABEL_BACKGROUND_SHADOW_RADIUS_DP,
                dy = LABEL_BACKGROUND_SHADOW_DY_DP,
                applyElevationOverlay = true,
            )
    val label =
        rememberTextComponent(
            color = MaterialTheme.colorScheme.onSurface,
            background = labelBackground,
            padding = Dimensions.of(8.dp, 4.dp),
            typeface = Typeface.MONOSPACE,
            textAlignment = Layout.Alignment.ALIGN_CENTER,
            minWidth = TextComponent.MinWidth.fixed(40.dp),
        )
    val indicatorFrontComponent =
        rememberShapeComponent(Shape.Pill, MaterialTheme.colorScheme.surface)
    val indicatorCenterComponent = rememberShapeComponent(Shape.Pill)
    val indicatorRearComponent = rememberShapeComponent(Shape.Pill)
    val indicator =
        rememberLayeredComponent(
            rear = indicatorRearComponent,
            front =
            rememberLayeredComponent(
                rear = indicatorCenterComponent,
                front = indicatorFrontComponent,
                padding = Dimensions.of(5.dp),
            ),
            padding = Dimensions.of(10.dp),
        )
    val guideline = rememberAxisGuidelineComponent()
    return remember(label, labelPosition, indicator, showIndicator, guideline) {
        object :
            DefaultCartesianMarker(
                label = label,
                valueFormatter = object : DefaultCartesianMarkerValueFormatter() {
                    override fun format(
                        context: CartesianDrawContext,
                        targets: List<CartesianMarker.Target>,
                    ): CharSequence {
                        return SpannableStringBuilder().apply {
                            targets.forEachIndexed { index, target ->
                                append2(target = target, shorten = targets.size > 1)
                                if (index != targets.lastIndex) append(", ")
                            }
                        }
                    }

                    private fun SpannableStringBuilder.append2(
                        target: CartesianMarker.Target,
                        shorten: Boolean,
                    ) {
                        when (target) {
                            is ColumnCartesianLayerMarkerTarget -> {
                                val values = mutableListOf<Pair<Float, Int>>()
                                target.columns.forEach { column ->
                                    if (column.entry.y != 0F) {
                                        values.add(column.entry.y to column.color)
                                    }
                                }
                                val includeSum = values.size > 1
                                if (includeSum) {
                                    append(target.columns.sumOf { it.entry.y })
                                    append(" (")
                                }
                                values.forEachIndexed { index, value ->
                                    append(value.first, value.second)
                                    if (index != values.lastIndex) {
                                        append(", ")
                                    }
                                }
                                if (includeSum) {
                                    append(")")
                                }
                            }

                            else -> append(target, shorten)
                        }
                    }
                },
                labelPosition = labelPosition,
                indicator = if (showIndicator) indicator else null,
                indicatorSizeDp = 36f,
                setIndicatorColor =
                if (showIndicator) {
                    { color ->
                        indicatorRearComponent.color = color.copyColor(alpha = .15f)
                        indicatorCenterComponent.color = color
                        indicatorCenterComponent.setShadow(radius = 12f, color = color)
                    }
                } else {
                    null
                },
                guideline = guideline,
            ) {

            override fun draw(
                context: CartesianDrawContext,
                targets: List<CartesianMarker.Target>,
            ) = with(context) {
                drawGuideline(targets)
                val halfIndicatorSize = indicatorSizeDp.half.pixels

                targets.forEach { target ->
                    when (target) {
                        is CandlestickCartesianLayerMarkerTarget -> {
                            drawIndicator(
                                target.canvasX,
                                target.openingCanvasY,
                                target.openingColor,
                                halfIndicatorSize
                            )
                            drawIndicator(
                                target.canvasX,
                                target.closingCanvasY,
                                target.closingColor,
                                halfIndicatorSize
                            )
                            drawIndicator(
                                target.canvasX,
                                target.lowCanvasY,
                                target.lowColor,
                                halfIndicatorSize
                            )
                            drawIndicator(
                                target.canvasX,
                                target.highCanvasY,
                                target.highColor,
                                halfIndicatorSize
                            )
                        }

                        is ColumnCartesianLayerMarkerTarget -> {
                            for (column in target.columns) {
                                if (column.entry.y == 0F) {
                                    continue
                                }
                                drawIndicator(
                                    target.canvasX,
                                    column.canvasY,
                                    column.color,
                                    halfIndicatorSize
                                )
                            }
                        }

                        is LineCartesianLayerMarkerTarget -> {
                            target.points.forEach { point ->
                                drawIndicator(
                                    target.canvasX,
                                    point.canvasY,
                                    point.color,
                                    halfIndicatorSize
                                )
                            }
                        }
                    }
                }
                drawLabel(context, targets)
            }


            override fun getInsets(
                context: CartesianMeasureContext,
                outInsets: Insets,
                horizontalDimensions: HorizontalDimensions,
            ) {
                with(context) {
                    super.getInsets(context, outInsets, horizontalDimensions)
                    val baseShadowInsetDp =
                        CLIPPING_FREE_SHADOW_RADIUS_MULTIPLIER * LABEL_BACKGROUND_SHADOW_RADIUS_DP
                    outInsets.top += (baseShadowInsetDp - LABEL_BACKGROUND_SHADOW_DY_DP).pixels
                    outInsets.bottom += (baseShadowInsetDp + LABEL_BACKGROUND_SHADOW_DY_DP).pixels
                }
            }
        }
    }
}

private const val LABEL_BACKGROUND_SHADOW_RADIUS_DP = 4f
private const val LABEL_BACKGROUND_SHADOW_DY_DP = 2f
private const val CLIPPING_FREE_SHADOW_RADIUS_MULTIPLIER = 1.4f