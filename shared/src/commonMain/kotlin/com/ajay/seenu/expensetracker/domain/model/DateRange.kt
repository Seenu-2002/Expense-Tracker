package com.ajay.seenu.expensetracker.domain.model

import com.ajay.seenu.expensetracker.util.toEpochMillis
import kotlinx.datetime.LocalDate

data class DateRange constructor(
    val start: LocalDate,
    val end: LocalDate
) {
    override fun toString(): String {
        return "DateRange(start=$start, end=$end, startInMillis=${start.toEpochMillis()}, endInMillis=${end.toEpochMillis()})"
    }

}