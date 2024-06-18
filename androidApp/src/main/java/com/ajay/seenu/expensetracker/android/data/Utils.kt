package com.ajay.seenu.expensetracker.android.data

import com.ajay.seenu.expensetracker.android.domain.data.Transaction
import com.ajay.seenu.expensetracker.android.domain.data.TransactionsByDate
import java.text.SimpleDateFormat

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