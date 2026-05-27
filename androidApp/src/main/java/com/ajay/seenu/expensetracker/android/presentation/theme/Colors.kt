package com.ajay.seenu.expensetracker.android.presentation.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * App-specific semantic colors that complement Material 3's [androidx.compose.material3.ColorScheme].
 *
 * Access via [LocalAppColors] or the [com.ajay.seenu.expensetracker.android.presentation.theme.AppTheme] helper.
 */
@Immutable
data class AppColors(
    val income: Color,
    val onIncome: Color,
    val incomeContainer: Color,
    val onIncomeContainer: Color,
    val expense: Color,
    val onExpense: Color,
    val expenseContainer: Color,
    val onExpenseContainer: Color,
    val transfer: Color,
    val onTransfer: Color,
    val transferContainer: Color,
    val onTransferContainer: Color,
    val warning: Color,
    val onWarning: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,
    val averagePercent: Color,
)

val LightAppColors = AppColors(
    income = LightIncome,
    onIncome = LightOnIncome,
    incomeContainer = LightIncomeContainer,
    onIncomeContainer = LightOnIncomeContainer,
    expense = LightExpense,
    onExpense = LightOnExpense,
    expenseContainer = LightExpenseContainer,
    onExpenseContainer = LightOnExpenseContainer,
    transfer = LightTransfer,
    onTransfer = LightOnTransfer,
    transferContainer = LightTransferContainer,
    onTransferContainer = LightOnTransferContainer,
    warning = LightWarning,
    onWarning = LightOnWarning,
    warningContainer = LightWarningContainer,
    onWarningContainer = LightOnWarningContainer,
    averagePercent = LightWarning,
)

val DarkAppColors = AppColors(
    income = DarkIncome,
    onIncome = DarkOnIncome,
    incomeContainer = DarkIncomeContainer,
    onIncomeContainer = DarkOnIncomeContainer,
    expense = DarkExpense,
    onExpense = DarkOnExpense,
    expenseContainer = DarkExpenseContainer,
    onExpenseContainer = DarkOnExpenseContainer,
    transfer = DarkTransfer,
    onTransfer = DarkOnTransfer,
    transferContainer = DarkTransferContainer,
    onTransferContainer = DarkOnTransferContainer,
    warning = DarkWarning,
    onWarning = DarkOnWarning,
    warningContainer = DarkWarningContainer,
    onWarningContainer = DarkOnWarningContainer,
    averagePercent = DarkWarning,
)

val LocalAppColors = staticCompositionLocalOf { LightAppColors }
