package com.ajay.seenu.expensetracker.util

import com.ajay.seenu.expensetracker.domain.model.DateRange
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
object DateRangeCalculator {

    /**
     * Calculates the current week range based on a specified start day
     * @param startDayOfWeek The day that should be considered the start of the week (1 = Monday, 7 = Sunday)
     * @return DateRange representing the current week
     */
    fun thisWeek(startDayOfWeek: Int = 1): DateRange {
        require(startDayOfWeek in 1..7) { "Start day of week must be between 1 (Monday) and 7 (Sunday)" }

        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val todayDayOfWeek = today.dayOfWeek.ordinal + 1 // Convert to 1-7 range

        // Calculate days to subtract to get to the start of week
        val daysToSubtract = if (todayDayOfWeek >= startDayOfWeek) {
            todayDayOfWeek - startDayOfWeek
        } else {
            7 - (startDayOfWeek - todayDayOfWeek)
        }

        val weekStart = today.minus(daysToSubtract, DateTimeUnit.DAY)
        val weekEnd = weekStart.plus(6, DateTimeUnit.DAY)

        return DateRange(weekStart, weekEnd)
    }

    /**
     * Calculates the current month range based on a specified start date
     * @param startDate The day of the month that should be considered the start (1-31)
     * @return DateRange representing the current month period
     */
    fun thisMonth(startDate: Int = 1): DateRange {
        require(startDate in 1..31) { "Start date must be between 1 and 31" }

        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val currentYear = today.year
        val currentMonth = today.monthNumber

        // Determine if we should use current month or previous month as the base
        val (baseYear, baseMonth) = if (today.dayOfMonth >= startDate) {
            Pair(currentYear, currentMonth)
        } else {
            if (currentMonth == 1) {
                Pair(currentYear - 1, 12)
            } else {
                Pair(currentYear, currentMonth - 1)
            }
        }

        // Create start date, handling cases where startDate doesn't exist in the month
        val daysInMonth = getDaysInMonth(baseYear, baseMonth)
        val actualStartDate = minOf(startDate, daysInMonth)

        val monthStart = LocalDate(baseYear, baseMonth, actualStartDate)

        // Calculate end date (day before start date of next month)
        val (nextYear, nextMonth) = if (baseMonth == 12) {
            Pair(baseYear + 1, 1)
        } else {
            Pair(baseYear, baseMonth + 1)
        }

        val nextMonthDays = getDaysInMonth(nextYear, nextMonth)
        val nextMonthStartDate = minOf(startDate, nextMonthDays)
        val monthEnd = LocalDate(nextYear, nextMonth, nextMonthStartDate).minus(1, DateTimeUnit.DAY)

        return DateRange(monthStart, monthEnd)
    }

    /**
     * Calculates the current year range based on a specified start month
     * @param startMonth The month that should be considered the start of the year (1-12)
     * @return DateRange representing the current year period
     */
    fun thisYear(startMonth: Int = 1): DateRange {
        require(startMonth in 1..12) { "Start month must be between 1 (January) and 12 (December)" }

        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val currentYear = today.year
        val currentMonth = today.monthNumber

        // Determine if we should use current year or previous year as the base
        val baseYear = if (currentMonth >= startMonth) {
            currentYear
        } else {
            currentYear - 1
        }

        val yearStart = LocalDate(baseYear, startMonth, 1)
        val yearEnd = LocalDate(baseYear + 1, startMonth, 1).minus(1, DateTimeUnit.DAY)

        return DateRange(yearStart, yearEnd)
    }

    private fun getDaysInMonth(year: Int, month: Int): Int {
        return when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if (isLeapYear(year)) 29 else 28
            else -> throw IllegalArgumentException("Invalid month: $month")
        }
    }

    private fun isLeapYear(year: Int): Boolean {
        return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
    }
}