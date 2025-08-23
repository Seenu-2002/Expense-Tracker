package com.ajay.seenu.expensetracker.domain.usecase.category

import com.ajay.seenu.expensetracker.data.repository.CategoryRepository
import com.ajay.seenu.expensetracker.domain.model.TransactionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class AddCategoryUseCase constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(
        label: String,
        type: TransactionType,
        res: Int,
        color: Long
    ) {
        withContext(Dispatchers.IO) {
            val searchCategories = repository.searchCategories(label, type)
            if (searchCategories.isNotEmpty()) {
                throw IllegalStateException("Category $label already present")
            }
            repository.addCategory(label, type, res, color)
        }
    }
}