package com.ajay.seenu.expensetracker.android.domain.usecases

import com.ajay.seenu.expensetracker.UserConfigurationsManager
import com.ajay.seenu.expensetracker.android.data.ExpenseByCategory
import com.ajay.seenu.expensetracker.android.data.ExpensePerDay
import com.ajay.seenu.expensetracker.android.data.TotalExpensePerDay
import com.ajay.seenu.expensetracker.android.data.TransactionRepository
import com.ajay.seenu.expensetracker.android.data.getDateFormat
import com.ajay.seenu.expensetracker.android.domain.data.Filter
import com.ajay.seenu.expensetracker.android.domain.data.Transaction
import com.ajay.seenu.expensetracker.android.domain.data.getDateRange
import javax.inject.Inject

class GetTotalTransactionPerDayByCategoryUseCase @Inject constructor(
    private val repository: TransactionRepository,
    private val userConfigurationsManager: UserConfigurationsManager
) {

    suspend operator fun invoke(startDayOfTheWeek: Int, filter: Filter): List<TotalExpensePerDay> {
        val totalExpenses = repository.getTotalTransactionPerDayByType(Transaction.Type.EXPENSE, filter.getDateRange(startDayOfTheWeek))
        return totalExpenses.mapData()
    }

    private suspend fun List<ExpensePerDay>.mapData(): List<TotalExpensePerDay> {
        val dateMap = hashMapOf<String, Double>()
        val map = hashMapOf<String, HashMap<Transaction.Category, Double>>()
        val possibleCategories = ArrayList<Transaction.Category>()

        val dateFormatter = userConfigurationsManager.getConfigs().getDateFormat()
        forEach { expensePerDay ->
            val category = expensePerDay.category
            val dateLabel = dateFormatter.format(expensePerDay.date)
            val amount = expensePerDay.amount

            if (!possibleCategories.contains(expensePerDay.category)) {
                possibleCategories.add(category)
            }

            dateMap[dateLabel]?.let {
                dateMap[dateLabel] = it + amount
                map[dateLabel]!!.let { categoryMap ->
                    categoryMap[category]?.let {
                        categoryMap[category] = it + amount
                    } ?: run {
                        categoryMap[category] = amount
                    }
                }
            } ?: run {
                dateMap[dateLabel] = amount
                map[dateLabel] = hashMapOf(category to amount)
            }
        }

        val totalExpensePerDay = map.entries.map { entry ->
            val date = dateFormatter.parse(entry.key)!!
            val amount = dateMap[entry.key]!!
            val expensesPerCategory = entry.value.map().let {
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

    private fun HashMap<Transaction.Category, Double>.map(): List<ExpenseByCategory> {
        return this.map { ExpenseByCategory(it.key, it.value) }
    }

    private fun fillEmptyExpensesForMissingCategories(possibleCategories: List<Transaction.Category>, list: List<ExpenseByCategory>): List<ExpenseByCategory> {
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