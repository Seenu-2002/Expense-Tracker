package com.ajay.seenu.expensetracker.android.data

import android.content.Context
import android.content.SharedPreferences
import com.ajay.seenu.expensetracker.android.domain.data.Filter
import com.ajay.seenu.expensetracker.android.domain.data.valueOf

object FilterPreference {

    private const val PREF_NAME = "FILTER_PREF"
    private const val PREF_MODE = Context.MODE_PRIVATE
    private const val CURRENT_FILTER = "CURRENT_FILTER"

    fun getCurrentFilter(context: Context): Filter {
        val filter = getPref(context)
            .getString(CURRENT_FILTER, Filter.All.key) ?: Filter.All.key
        return Filter.valueOf(filter)
    }

    fun setCurrentFilter(context: Context, filter: Filter) {
        getPref(context)
            .edit()
            .putString(CURRENT_FILTER, filter.key)
            .apply()
    }

    private fun getPref(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, PREF_MODE)
    }

}