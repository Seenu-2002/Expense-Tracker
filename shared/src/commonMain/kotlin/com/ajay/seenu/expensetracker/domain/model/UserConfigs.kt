package com.ajay.seenu.expensetracker.domain.model

import kotlinx.datetime.DayOfWeek

data class UserConfigs constructor(
    val name: String,
    val userImagePath: String?,
    val theme: Theme,
    val weekStartsFrom: DayOfWeek,
    val dateFormat: String,
    val isAppLockEnabled: Boolean
)

enum class Theme {
    LIGHT, DARK, SYSTEM_THEME
}