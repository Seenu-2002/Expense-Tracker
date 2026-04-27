package com.ajay.seenu.expensetracker.util

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun LocalDate.toEpochMillis(timeZone: TimeZone = TimeZone.currentSystemDefault()): Long {
    return this.atStartOfDayIn(timeZone).toEpochMilliseconds()
}

private val localDateTimeFormater by lazy {
    LocalDateTime.Format {
        year()
        char('/')
        monthNumber()
        char('/')
        day()
    }
}

private val localDateFormater by lazy {
    LocalDate.Format {
        year()
        char('/')
        monthNumber()
        char('/')
        day()
    }
}

private val sectionDateFormatter by lazy {
    LocalDate.Format {
        day()
        char(' ')
        monthName(MonthNames.ENGLISH_ABBREVIATED)
        chars(", ")
        year()
    }
}

fun String.toLocalDate(): LocalDate {
    return LocalDate.parse(this, localDateFormater)
}

@OptIn(ExperimentalTime::class)
fun Instant.getDateLabel(timeZone: TimeZone = TimeZone.currentSystemDefault()): String {
    return localDateTimeFormater.format(this.toLocalDateTime(timeZone))
}

fun LocalDate.getDateLabel(): String {
    return localDateFormater.format(this)
}

@OptIn(ExperimentalTime::class)
fun LocalDate.toSectionLabel(timeZone: TimeZone = TimeZone.currentSystemDefault()): String {
    val today = Clock.System.now().toLocalDateTime(timeZone).date
    return when (this) {
        today -> "Today"
        today.minus(1, DateTimeUnit.DAY) -> "Yesterday"
        else -> sectionDateFormatter.format(this)
    }
}

@OptIn(ExperimentalTime::class)
fun Long.toLocalDateTime(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDateTime {
    val instant = Instant.fromEpochMilliseconds(this)
    return instant.toLocalDateTime(timeZone)
}

fun Long.toLocalDate(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDate {
    return this.toLocalDateTime(timeZone).date
}