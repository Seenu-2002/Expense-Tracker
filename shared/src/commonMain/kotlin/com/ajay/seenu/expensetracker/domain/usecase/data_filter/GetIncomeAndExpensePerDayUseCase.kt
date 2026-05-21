package com.ajay.seenu.expensetracker.domain.usecase.data_filter

import com.ajay.seenu.expensetracker.data.repository.TransactionRepository
import com.ajay.seenu.expensetracker.domain.model.DailyIncomeExpense
import com.ajay.seenu.expensetracker.domain.model.DateRange
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil

class GetIncomeAndExpensePerDayUseCase(
    private val repository: TransactionRepository
) {

    suspend operator fun invoke(dateRange: DateRange): List<DailyIncomeExpense> {
        val daily = repository.getIncomeAndExpensePerDay(dateRange)
        val daysSpanned = dateRange.start.daysUntil(dateRange.end)
        return if (daysSpanned > MONTHLY_BUCKET_THRESHOLD_DAYS) {
            bucketByMonth(daily)
        } else {
            daily
        }
    }

    private fun bucketByMonth(daily: List<DailyIncomeExpense>): List<DailyIncomeExpense> {
        if (daily.isEmpty()) return daily
        return daily
            .groupBy { LocalDate(it.date.year, it.date.monthNumber, 1) }
            .map { (monthStart, entries) ->
                DailyIncomeExpense(
                    date = monthStart,
                    income = entries.sumOf { it.income },
                    expense = entries.sumOf { it.expense },
                )
            }
            .sortedBy { it.date }
    }

    companion object {
        private const val MONTHLY_BUCKET_THRESHOLD_DAYS = 90
    }
}
