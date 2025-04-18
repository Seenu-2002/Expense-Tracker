package com.ajay.seenu.expensetracker.android.data

import com.ajay.seenu.expensetracker.android.domain.data.Transaction
import com.ajay.seenu.expensetracker.android.domain.data.TransactionsByDate
import com.ajay.seenu.expensetracker.entity.StartDayOfTheWeek
import com.ajay.seenu.expensetracker.entity.UserConfigs
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

const val PER_PAGE = 100

fun List<Transaction>.sortByDate(dateFormat: SimpleDateFormat): List<TransactionsByDate> {
    val map = hashMapOf<String, ArrayList<Transaction>>()
    this.forEach { transaction ->
        val dateLabel = dateFormat.format(transaction.date)
        map[dateLabel]?.add(transaction) ?: run {
            map[dateLabel] = arrayListOf(transaction)
        }
    }
    return map.entries.map {
        TransactionsByDate(dateFormat.parse(it.key)!!, it.key, it.value.sortedByDescending { transaction -> transaction.date })
    }.sortedByDescending {
        it.rawDate
    }
}

// FIXME: It should be calculator using the start day of the week from user configuration
// FIXME: Issue when user config added. Fix it and add unit cases
fun getThisWeekInMillis(startDay: Int, calendar: Calendar = Calendar.getInstance()): Pair<Long, Long> {
    val currentDay = calendar.get(Calendar.DAY_OF_WEEK)

    calendar.set(Calendar.DAY_OF_WEEK, startDay)
    calendar.resetTime()
    val weekStart = calendar.timeInMillis

    calendar.add(Calendar.DAY_OF_WEEK, 7)
    calendar.tillMidNight()
    val weekEnd = calendar.timeInMillis

    // Adjust for days before Sunday of the current week
    return Pair(weekStart, weekEnd)
}

fun getThisMonthInMillis(calendar: Calendar = Calendar.getInstance()): Pair<Long, Long> {
    // Adjust calendar to first day of the month
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    calendar.resetTime()
    val monthStart = calendar.timeInMillis

    // Move to the last day by adding maximum days in a month
    calendar.add(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE))
    calendar.tillMidNight()
    val monthEnd = calendar.timeInMillis

    return Pair(monthStart, monthEnd)
}

fun getThisYearInMillis(calendar: Calendar = Calendar.getInstance()): Pair<Long, Long> {
    val year = calendar.get(Calendar.YEAR)

    val startOfYear = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.MONTH, Calendar.JANUARY)
        resetTime()
    }.timeInMillis

    val endOfYear = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.DAY_OF_MONTH, 31)
        set(Calendar.MONTH, Calendar.DECEMBER)
        tillMidNight()
    }.timeInMillis

    return Pair(startOfYear, endOfYear)
}

fun Calendar.resetTime() {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}

fun Calendar.tillMidNight() {
    set(Calendar.HOUR_OF_DAY, 23)
    set(Calendar.MINUTE, 59)
    set(Calendar.SECOND, 59)
    set(Calendar.MILLISECOND, 999)
}

private const val MILLIS_IN_DAY = 1000 * 60 * 60 * 24

internal fun UserConfigs.getStartDayOfTheWeek(): Int {
    return when (weekStartsFrom) {
        StartDayOfTheWeek.MONDAY -> Calendar.MONDAY
        StartDayOfTheWeek.SUNDAY -> Calendar.SUNDAY
    }
}

internal fun UserConfigs.getDateFormat(): SimpleDateFormat {
    return SimpleDateFormat(dateFormat, Locale.ENGLISH)
}