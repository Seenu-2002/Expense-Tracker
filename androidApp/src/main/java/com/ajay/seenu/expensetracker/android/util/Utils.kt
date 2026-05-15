package com.ajay.seenu.expensetracker.android.util

import android.icu.text.DecimalFormat
import android.icu.text.DecimalFormatSymbols
import com.ajay.seenu.expensetracker.domain.model.UserConfigs
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


// TODO: Move this function to appropriate class and should not be inside domain
inline fun <T> Iterable<T>.sumOf(selector: (T) -> Float): Float {
    var sum = 0f
    for (element in this) {
        sum += selector(element)
    }
    return sum
}

fun formatDateHeader(inputDate: String): String {
    // TODO: User data format configuration
    val formatter = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
    val parsedDate = formatter.parse(inputDate) ?: return inputDate

    val calendar = Calendar.getInstance()
    calendar.time = parsedDate

    val today = Calendar.getInstance()
    today.set(Calendar.HOUR_OF_DAY, 0)
    today.set(Calendar.MINUTE, 0)
    today.set(Calendar.SECOND, 0)
    today.set(Calendar.MILLISECOND, 0)

    val yesterday = Calendar.getInstance()

    yesterday.time = today.time
    yesterday.add(Calendar.DAY_OF_YEAR, -1)

    return when (calendar.timeInMillis) {
        today.timeInMillis -> "Today"
        yesterday.timeInMillis -> "Yesterday"
        else -> inputDate
    }
}

private val amountFormatter = DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale.getDefault()))

fun Double.formatAmount(): String = amountFormatter.format(this)

fun Double.asCurrency(symbol: String): String = "$symbol${formatAmount()}"

fun UserConfigs.getDateFormat(): SimpleDateFormat {
    return SimpleDateFormat(dateFormat, Locale.ENGLISH)
}