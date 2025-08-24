package com.ajay.seenu.expensetracker.domain.usecase.data_filter

import com.ajay.seenu.expensetracker.data.repository.CategoryRepository
import com.ajay.seenu.expensetracker.data.repository.TransactionRepository
import com.ajay.seenu.expensetracker.domain.model.DateRange
import com.ajay.seenu.expensetracker.domain.model.TotalExpensePerCategory
import com.ajay.seenu.expensetracker.domain.model.TransactionType

class GetExpenseByCategoryUseCase constructor(
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository
) {

    suspend operator fun invoke(dateRange: DateRange): List<TotalExpensePerCategory> {
        val categories = categoryRepository.getCategories(TransactionType.EXPENSE)
        return transactionRepository.getExpensePerDayByCategory(dateRange).map {
            val category = categories.find { category -> category.id == it.categoryId }!!
            TotalExpensePerCategory(category, it.totalAmount ?: 0.0)
        }
    }

}