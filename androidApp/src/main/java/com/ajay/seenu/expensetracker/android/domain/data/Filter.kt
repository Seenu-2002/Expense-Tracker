package com.ajay.seenu.expensetracker.android.domain.data

import com.ajay.seenu.expensetracker.android.data.getThisMonthInMillis
import com.ajay.seenu.expensetracker.android.data.getThisWeekInMillis
import com.ajay.seenu.expensetracker.android.data.getThisYearInMillis

sealed class Filter(val key: String) {
    companion object {  }

    data object All: Filter("All")
    data object ThisWeek: Filter("ThisWeek")
    data object ThisMonth: Filter("ThisMonth")
    data object ThisYear: Filter("ThisYear")
}

internal fun Filter.Companion.valueOf(str: String): Filter {
    return when (str) {
        Filter.All.key -> Filter.All
        Filter.ThisYear.key -> Filter.ThisYear
        Filter.ThisMonth.key -> Filter.ThisMonth
        Filter.ThisWeek.key -> Filter.ThisWeek
        else -> throw Exception("Invalid type")
    }
}

// TODO: Should be in common main using expect actual
internal fun Filter.getDateRange(): Pair<Long, Long>? {
    return when (this) {
        Filter.All -> {
            null
        }
        Filter.ThisYear -> {
            getThisYearInMillis()
        }
        Filter.ThisWeek -> {
            getThisWeekInMillis()
        }
        Filter.ThisMonth -> {
            getThisMonthInMillis()
        }
    }
}