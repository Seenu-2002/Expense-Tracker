package com.ajay.seenu.expensetracker.domain.model

import com.ajay.seenu.expensetracker.util.toEpochMillis
import com.ajay.seenu.expensetracker.util.toLocalDate
import kotlinx.datetime.LocalDate

sealed interface DateFilter {
    data object ThisWeek : DateFilter
    data object ThisMonth : DateFilter
    data object ThisYear : DateFilter
    data class Custom constructor(val startDate: LocalDate, val endDate: LocalDate) : DateFilter {

        val startDateInMillis = startDate.toEpochMillis()
        val endDateInMillis = endDate.toEpochMillis()

        companion object {
            val MOCK = Custom(
                "2022/02/21".toLocalDate(),
                "2023/02/21".toLocalDate()
            )
        }

    }
}