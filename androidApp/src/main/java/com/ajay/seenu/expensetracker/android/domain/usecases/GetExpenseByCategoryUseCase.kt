package com.ajay.seenu.expensetracker.android.domain.usecases

import com.ajay.seenu.expensetracker.android.data.TotalExpensePerCategory
import com.ajay.seenu.expensetracker.android.data.TransactionRepository
import com.ajay.seenu.expensetracker.android.domain.data.Filter
import com.ajay.seenu.expensetracker.android.domain.data.Transaction
import com.ajay.seenu.expensetracker.android.domain.data.getDateRange
import com.ajay.seenu.expensetracker.android.domain.mapper.CategoryMapper
import javax.inject.Inject

class GetExpenseByCategoryUseCase @Inject constructor(
    private val repository: TransactionRepository,
) {

    suspend operator fun invoke(startDayOfTheWeek: Int, filter: Filter): List<TotalExpensePerCategory> {
        val categories = repository.getCategories(Transaction.Type.EXPENSE).let {
            CategoryMapper.mapCategories(it)
        }
        val range = filter.getDateRange(startDayOfTheWeek)
        return repository.getExpensePerDayByCategory(range).map {
            val category = categories.find { category -> category.id == it.category }!!.let { category ->
                Transaction.Category(
                    category.id,
                    category.type,
                    category.label,
                    category.parent
                )
            }
            TotalExpensePerCategory(category, it.totalAmount ?: 0.0)
        }
    }

}