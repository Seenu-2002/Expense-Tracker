package com.ajay.seenu.expensetracker.domain.usecase.data_filter

import com.ajay.seenu.expensetracker.data.repository.TransactionRepository
import com.ajay.seenu.expensetracker.domain.model.Category
import com.ajay.seenu.expensetracker.domain.model.DateRange
import com.ajay.seenu.expensetracker.domain.model.ExpenseByCategory
import com.ajay.seenu.expensetracker.domain.model.ExpensePerDay
import com.ajay.seenu.expensetracker.domain.model.TotalExpensePerDay
import com.ajay.seenu.expensetracker.domain.model.TransactionType
import com.ajay.seenu.expensetracker.util.getDateLabel
import com.ajay.seenu.expensetracker.util.toLocalDate
import kotlin.time.ExperimentalTime

class GetTotalTransactionPerDayByCategoryUseCase constructor(
    private val repository: TransactionRepository
) {

    suspend operator fun invoke(
        dateRange: DateRange
    ): List<TotalExpensePerDay> {
        val totalExpenses =
            repository.getTotalTransactionPerDayByType(TransactionType.EXPENSE, dateRange)
        return totalExpenses.mapData()
    }

    @OptIn(ExperimentalTime::class)
    private fun List<ExpensePerDay>.mapData(): List<TotalExpensePerDay> {
        val dateMap = hashMapOf<String, Double>()
        val map = hashMapOf<String, HashMap<Category, Double>>()
        val possibleCategories = ArrayList<Category>()

        forEach { expensePerDay ->
            val category = expensePerDay.category
            val dateLabel = expensePerDay.date.getDateLabel()
            val amount = expensePerDay.amount

            if (!possibleCategories.contains(expensePerDay.category)) {
                possibleCategories.add(category)
            }

            dateMap[dateLabel]?.let {
                dateMap[dateLabel] = it + amount
                map[dateLabel]!!.let { categoryMap ->
                    categoryMap[category] = categoryMap[category]?.let { total ->
                        total + amount
                    } ?: amount
                }
            } ?: run {
                dateMap[dateLabel] = amount
                map[dateLabel] = hashMapOf(category to amount)
            }
        }

        val totalExpensePerDay = map.entries.map { (dateLabel, categoryMap) ->
            val amount = dateMap[dateLabel]!!
            val date = dateLabel.toLocalDate()
            val expensesPerCategory = categoryMap.map().let {
                fillEmptyExpensesForMissingCategories(possibleCategories, it)
            }
            TotalExpensePerDay(
                date,
                amount,
                expensesPerCategory
            )
        }
        return totalExpensePerDay
    }

    private fun HashMap<Category, Double>.map(): List<ExpenseByCategory> {
        return this.map { ExpenseByCategory(it.key, it.value) }
    }

    private fun fillEmptyExpensesForMissingCategories(
        possibleCategories: List<Category>,
        list: List<ExpenseByCategory>
    ): List<ExpenseByCategory> {
        val mutableList = list.toMutableList()
        for (possibleCategory in possibleCategories) {
            if (mutableList.find { it.category == possibleCategory } != null) {
                continue
            }
            mutableList.add(ExpenseByCategory.getEmpty(possibleCategory))
        }
        return mutableList.sortedBy { it.category.label }
    }
}