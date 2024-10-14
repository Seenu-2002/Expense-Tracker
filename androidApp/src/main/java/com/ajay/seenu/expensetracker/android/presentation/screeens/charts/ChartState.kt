package com.ajay.seenu.expensetracker.android.presentation.screeens.charts

sealed interface ChartState {
    data object Empty : ChartState
    data object Fetching : ChartState
    data object Success : ChartState
    sealed interface Failed : ChartState {
        data object InSufficientData : Failed
        data class Unhandled(val exp: Exception) : Failed
    }
}