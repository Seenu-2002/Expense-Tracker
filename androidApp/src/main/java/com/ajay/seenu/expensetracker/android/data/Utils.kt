package com.ajay.seenu.expensetracker.android.data

import com.ajay.seenu.expensetracker.android.domain.data.Transaction
import com.ajay.seenu.expensetracker.android.domain.data.TransactionsByDate
import java.text.SimpleDateFormat
import java.util.Calendar

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

fun getThisWeekInMillis(calendar: Calendar = Calendar.getInstance()): Pair<Long, Long> {
    val currentDay = calendar.get(Calendar.DAY_OF_WEEK)

    // Adjust calendar to point to Sunday of the current week
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
    val weekStart = calendar.timeInMillis

    // Move to the next Saturday (end of the week)
    calendar.add(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
    val weekEnd = calendar.timeInMillis

    // Adjust for days before Sunday of the current week
    return Pair(weekStart - (currentDay - Calendar.SUNDAY) * MILLIS_IN_DAY, weekEnd)
}

fun getThisYearInMillis(calendar: Calendar = Calendar.getInstance()): Pair<Long, Long> {
    val year = calendar.get(Calendar.YEAR)

    val startOfYear = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.MONTH, Calendar.JANUARY)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val endOfYear = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.DAY_OF_MONTH, 31)
        set(Calendar.MONTH, Calendar.DECEMBER)
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }.timeInMillis

    return Pair(startOfYear, endOfYear)
}

private const val MILLIS_IN_DAY = 1000 * 60 * 60 * 24