package com.ajay.seenu.expensetracker.domain.usecase

import com.ajay.seenu.expensetracker.UserConfigurationsManager
import com.ajay.seenu.expensetracker.domain.model.DateFilter
import com.ajay.seenu.expensetracker.domain.model.DateRange
import com.ajay.seenu.expensetracker.util.DateRangeCalculator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class DateRangeCalculatorUseCase constructor(
    private val userConfigurationsManager: UserConfigurationsManager
) {

    suspend operator fun invoke(filter: DateFilter): DateRange {
        return withContext(Dispatchers.IO) {
            val userConfigs = userConfigurationsManager.getConfigs()
            when (filter) {
                DateFilter.ThisWeek -> {
                    DateRangeCalculator.thisWeek(userConfigs.weekStartsFrom.ordinal)
                }

                DateFilter.ThisMonth -> {
                    // TODO: Custom Start date support
                    DateRangeCalculator.thisMonth()
                }

                DateFilter.ThisYear -> {
                    // TODO: Custom Start month support
                    DateRangeCalculator.thisYear()
                }

                is DateFilter.Custom -> {
                    DateRange(filter.startDate, filter.endDate)
                }
            }
        }
    }

}