package com.ajay.seenu.expensetracker.domain.model

import kotlinx.datetime.LocalDate

data class DateRange constructor(
    val start: LocalDate,
    val end: LocalDate
)