package com.ajay.seenu.expensetracker.android.domain.data

import com.ajay.seenu.expensetracker.android.data.getThisMonthInMillis
import com.ajay.seenu.expensetracker.android.data.getThisWeekInMillis
import com.ajay.seenu.expensetracker.android.data.getThisYearInMillis

sealed class Filter(val key: String) {
    companion object {
        val CUSTOM_MOCK = Custom(0L, 0L)
    }

    data object ThisWeek: Filter("ThisWeek")
    data object ThisMonth: Filter("ThisMonth")
    data object ThisYear: Filter("ThisYear")
    data class Custom(val startDate: Long, val endDate: Long) : Filter("$startDate,$endDate")
}

internal fun Filter.Companion.valueOf(str: String): Filter {
    return when (str) {
        Filter.ThisYear.key -> Filter.ThisYear
        Filter.ThisMonth.key -> Filter.ThisMonth
        Filter.ThisWeek.key -> Filter.ThisWeek
        else -> {
            try {
                val arr = str.split(",")
                return Filter.Custom(arr.first().toLong(), arr.last().toLong())
            } catch (exp: Throwable) {
                // TODO: Record non-fatal exception
                Filter.ThisMonth
            }
        }
    }
}

// TODO: Should be in common main using expect actual
internal fun Filter.getDateRange(): Pair<Long, Long> {
    return when (this) {
        Filter.ThisYear -> {
            getThisYearInMillis()
        }
        Filter.ThisWeek -> {
            getThisWeekInMillis()
        }
        Filter.ThisMonth -> {
            getThisMonthInMillis()
        }
        is Filter.Custom -> {
            return Pair(startDate, endDate)
        }
    }
}