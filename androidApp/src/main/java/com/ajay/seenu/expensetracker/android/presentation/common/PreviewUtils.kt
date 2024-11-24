package com.ajay.seenu.expensetracker.android.presentation.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.ajay.seenu.expensetracker.android.presentation.theme.AppDefaults
import com.ajay.seenu.expensetracker.android.presentation.theme.LocalColors

@Composable
fun PreviewThemeWrapper(content: @Composable () -> Unit) {
    val colors = AppDefaults.colors()
    CompositionLocalProvider(
        LocalColors provides colors
    ) {
        content.invoke()
    }
}