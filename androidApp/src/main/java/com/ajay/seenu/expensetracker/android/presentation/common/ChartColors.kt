package com.ajay.seenu.expensetracker.android.presentation.common

import androidx.compose.ui.graphics.Color

/**
 * Categorical palette for charts (pies, donuts, multi-series bars) where colors are picked by index.
 *
 * For semantic colors (income/expense/transfer/warning), use
 * [com.ajay.seenu.expensetracker.android.presentation.theme.AppTheme.colors] inside a Composable.
 */
object ChartDefaults {
    val dynamicColors = listOf(
        Color(0xFF64B5F6), // Light Sky Blue
        Color(0xFF81C784), // Soft Green
        Color(0xFFFFF176), // Pale Yellow
        Color(0xFFFF8A65), // Soft Coral
        Color(0xFF9575CD), // Lavender Purple
        Color(0xFF4DD0E1), // Aqua Blue
        Color(0xFF90CAF9), // Light Blue
        Color(0xFF80CBC4), // Teal Pastel
        Color(0xFFAED581), // Light Green
        Color(0xFFFFF59D), // Light Yellow
        Color(0xFFFFA726), // Vibrant Orange
        Color(0xFFB39DDB), // Soft Lavender
        Color(0xFF7986CB), // Muted Indigo
        Color(0xFF4FC3F7), // Light Azure
        Color(0xFF64FFDA), // Light Aqua
        Color(0xFFDCE775), // Lemon Green
        Color(0xFFFFD54F), // Golden Yellow
        Color(0xFFFF7043), // Warm Orange
        Color(0xFFBA68C8), // Soft Violet
        Color(0xFF81D4FA), // Pastel Blue
        Color(0xFFE1BEE7), // Pale Violet
        Color(0xFFFFCC80), // Soft Apricot
        Color(0xFFE6EE9C), // Pale Lime
        Color(0xFFFFF9C4), // Soft Lemon
        Color(0xFFB3E5FC), // Pale Blue
        Color(0xFFCE93D8), // Pale Mauve
        Color(0xFFB2DFDB), // Pale Aqua
        Color(0xFFFFE082), // Pastel Yellow
        Color(0xFFFFAB91), // Light Peach
        Color(0xFFD1C4E9)  // Lavender Mist
    )

    fun getDynamicColor(index: Int): Color {
        return dynamicColors[index % dynamicColors.size]
    }
}
