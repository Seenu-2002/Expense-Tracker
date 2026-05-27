package com.ajay.seenu.expensetracker.android.presentation.common

import androidx.compose.runtime.Composable
import com.ajay.seenu.expensetracker.android.presentation.theme.ExpenseTrackerTheme

@Composable
fun PreviewThemeWrapper(content: @Composable () -> Unit) {
    ExpenseTrackerTheme {
        content()
    }
}
