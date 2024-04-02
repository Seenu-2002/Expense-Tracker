package com.ajay.seenu.expensetracker.android.presentation.navigation

sealed class Screen(val route: String) {
    data object Overview : Screen("overview_route")
    data object Analytics : Screen("analytics_route")
    data object Settings : Screen("settings_route")
}