package com.ajay.seenu.expensetracker.entity

data class UserConfigs(
    val name: String,
    val userImagePath: String?,
    val theme: Theme,
    val weekStartsFrom: StartDayOfTheWeek,
    val dateFormat: String,
    val isAppLockEnabled: Boolean
)

enum class Theme {
    LIGHT, DARK, SYSTEM_THEME
}

enum class StartDayOfTheWeek {
    SUNDAY, MONDAY
}