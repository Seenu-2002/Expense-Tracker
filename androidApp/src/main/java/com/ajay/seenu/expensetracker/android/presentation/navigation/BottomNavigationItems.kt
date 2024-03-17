package com.ajay.seenu.expensetracker.android.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavigationItems(
    val label: String,
    val icon: ImageVector = Icons.Filled.Home,
    val route: String = ""
)

fun getBottomNavigationItems(): List<BottomNavigationItems> {
    return arrayListOf<BottomNavigationItems>().apply {
        add(BottomNavigationItems(
            label = "Overview",
            route = Screen.Overview.route
        ))
        add(BottomNavigationItems(
            label = "Analytics",
            icon = Icons.Filled.Face,
            route = Screen.Analytics.route
        ))
        add(BottomNavigationItems(
            label = "Profile",
            icon = Icons.Filled.Person,
            route = Screen.Settings.route
        ))
    }
}