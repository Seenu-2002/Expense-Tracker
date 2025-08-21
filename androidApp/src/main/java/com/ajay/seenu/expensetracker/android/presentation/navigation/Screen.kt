package com.ajay.seenu.expensetracker.android.presentation.navigation

sealed class Screen(val route: String) {
    data object Overview : Screen("overview_route")
    data object Analytics : Screen("analytics_route")
    data object Settings : Screen("settings_route")
    data object AddTransaction : Screen("add_transaction")
    data object DetailTransaction: Screen("detail_transaction")
    data object CategoryList : Screen("category_list_route")
    data object ChangeCategoryInTransaction : Screen("change_category_in_transaction")
    data object Category: Screen("category_route")
    data object Default : Screen("default")
    data object Budget : Screen("budget_route")
    data object AddBudget : Screen("budget")
    data object BudgetDetail : Screen("budget_detail")
}