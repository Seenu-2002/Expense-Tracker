package com.ajay.seenu.expensetracker.android.domain.data

sealed class Filter {
    data object All: Filter()
    data object ThisWeek: Filter()
    data object ThisYear: Filter()
}