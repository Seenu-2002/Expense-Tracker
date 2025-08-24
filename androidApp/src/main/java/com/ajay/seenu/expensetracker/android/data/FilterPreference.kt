package com.ajay.seenu.expensetracker.android.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.ajay.seenu.expensetracker.domain.model.DateFilter
import com.ajay.seenu.expensetracker.util.getDateLabel
import com.ajay.seenu.expensetracker.util.toLocalDate

// TODO: Move to shared as both platforms can use it
object FilterPreference {

    private const val PREF_NAME = "FILTER_PREF"
    private const val PREF_MODE = Context.MODE_PRIVATE
    private const val CURRENT_FILTER = "CURRENT_FILTER"

    fun getCurrentFilter(context: Context): DateFilter {
        val filterValue = getPref(context)
            .getString(CURRENT_FILTER, null) ?: return DateFilter.ThisMonth
        return getFilter(filterValue)
    }

    private fun getFilter(value: String): DateFilter {
        return when (value) {
            "ThisWeek" -> DateFilter.ThisWeek
            "ThisMonth" -> DateFilter.ThisMonth
            "ThisYear" -> DateFilter.ThisYear
            else -> {
                val parts = value.split(",")
                DateFilter.Custom(parts[0].toLocalDate(), parts[1].toLocalDate())
            }
        }
    }

    fun setCurrentFilter(context: Context, filter: DateFilter) {
        val value =
            when (filter) {
                DateFilter.ThisWeek -> "ThisWeek"
                DateFilter.ThisMonth -> "ThisMonth"
                DateFilter.ThisYear -> "ThisYear"
                is DateFilter.Custom -> {
                    "${filter.startDate.getDateLabel()},${filter.endDate.getDateLabel()}"
                }
            }
        getPref(context)
            .edit {
                putString(CURRENT_FILTER, value)
            }
    }

    private fun getPref(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, PREF_MODE)
    }

}