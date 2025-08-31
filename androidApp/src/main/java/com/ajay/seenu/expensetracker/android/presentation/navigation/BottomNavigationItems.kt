package com.ajay.seenu.expensetracker.android.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Money
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavigationItems(
    val label: String,
    val filledIcon: ImageVector = Icons.Filled.Home,
    val outlinedIcon: ImageVector = Icons.Outlined.Home,
    val route: String = ""
)

fun getBottomNavigationItems(): List<BottomNavigationItems> {
    return arrayListOf<BottomNavigationItems>().apply {
        add(BottomNavigationItems(
            label = "Overview",
            filledIcon = Icons.Filled.Home,
            outlinedIcon = Icons.Outlined.Home,
            route = Screen.Overview.route
        ))
        add(BottomNavigationItems(
            label = "Analytics",
            filledIcon = Icons.Filled.Analytics,
            outlinedIcon = Icons.Outlined.Analytics,
            route = Screen.Analytics.route
        ))
        add(BottomNavigationItems(
            label = "Budget",
            filledIcon = Icons.Filled.Money,
            outlinedIcon = Icons.Outlined.Money,
            route = Screen.Budget.route
        ))
        add(BottomNavigationItems(
            label = "Settings",
            filledIcon = Icons.Filled.Settings,
            outlinedIcon = Icons.Outlined.Settings,
            route = Screen.Settings.route
        ))
    }
}