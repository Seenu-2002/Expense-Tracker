package com.ajay.seenu.expensetracker.entity.budget

import kotlinx.datetime.LocalDate
import kotlinx.datetime.atTime
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

data class BudgetPeriod(
    val startDate: Long,
    val endDate: Long
) {
    companion object {
        fun getCurrentPeriod(
            periodType: BudgetPeriodType,
            currentTimeMillis: Long = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        ): BudgetPeriod {
            val currentInstant = kotlinx.datetime.Instant.fromEpochMilliseconds(currentTimeMillis)
            val currentDateTime = currentInstant.toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())

            return when (periodType) {
                BudgetPeriodType.WEEKLY -> {
                    val startOfWeek = currentDateTime.date.minus(
                        kotlinx.datetime.DatePeriod(days = currentDateTime.dayOfWeek.ordinal)
                    )
                    val startOfWeekDateTime = startOfWeek.atTime(0, 0)
                    val endOfWeekDateTime = startOfWeekDateTime.date.plus(
                        kotlinx.datetime.DatePeriod(days = 6)
                    ).atTime(23, 59, 59, 999_000_000)

                    BudgetPeriod(
                        startDate = startOfWeekDateTime.toInstant(kotlinx.datetime.TimeZone.currentSystemDefault()).toEpochMilliseconds(),
                        endDate = endOfWeekDateTime.toInstant(kotlinx.datetime.TimeZone.currentSystemDefault()).toEpochMilliseconds()
                    )
                }

                BudgetPeriodType.MONTHLY -> {
                    val startOfMonth = LocalDate(
                        year = currentDateTime.date.year,
                        monthNumber = currentDateTime.date.monthNumber,
                        dayOfMonth = 1
                    )
                    val startOfMonthDateTime = startOfMonth.atTime(0, 0)
                    val endOfMonthDateTime = startOfMonth.plus(kotlinx.datetime.DatePeriod(months = 1))
                        .minus(kotlinx.datetime.DatePeriod(days = 1))
                        .atTime(23, 59, 59, 999_000_000)

                    BudgetPeriod(
                        startDate = startOfMonthDateTime.toInstant(kotlinx.datetime.TimeZone.currentSystemDefault()).toEpochMilliseconds(),
                        endDate = endOfMonthDateTime.toInstant(kotlinx.datetime.TimeZone.currentSystemDefault()).toEpochMilliseconds()
                    )
                }

                BudgetPeriodType.YEARLY -> {
                    val startOfYear = LocalDate(
                        year = currentDateTime.date.year,
                        monthNumber = 1,
                        dayOfMonth = 1
                    )
                    val startOfYearDateTime = startOfYear.atTime(0, 0)
                    val endOfYearDateTime = LocalDate(
                        year = currentDateTime.date.year,
                        monthNumber = 12,
                        dayOfMonth = 31
                    ).atTime(23, 59, 59, 999_000_000)

                    BudgetPeriod(
                        startDate = startOfYearDateTime.toInstant(kotlinx.datetime.TimeZone.currentSystemDefault()).toEpochMilliseconds(),
                        endDate = endOfYearDateTime.toInstant(kotlinx.datetime.TimeZone.currentSystemDefault()).toEpochMilliseconds()
                    )
                }

                BudgetPeriodType.CUSTOM -> {
                    // For custom periods, you'll need to handle this based on your specific needs
                    BudgetPeriod(currentTimeMillis, currentTimeMillis)
                }
            }
        }
    }
}