package com.ajay.seenu.expensetracker.android.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

internal val LocalColors = staticCompositionLocalOf<AppColors> {
    error("App colors not found")
}

object AppDefaults {
    @Composable
    fun colors(
        incomeColor: Color = Color(0xFF00A86B),
        expenseColor: Color = Color(0xFFFD3C4A),
        averagePercentColor: Color = Color(0xFFFCAC12)
    ) = AppColors(
        incomeColor = incomeColor,
        expenseColor = expenseColor,
        averagePercentColor = averagePercentColor
    )
}

data class AppColors(
    val incomeColor: Color,
    val expenseColor: Color,
    val averagePercentColor: Color
)