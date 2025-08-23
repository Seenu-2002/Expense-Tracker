package com.ajay.seenu.expensetracker.domain.usecase.category

import com.ajay.seenu.expensetracker.data.repository.CategoryRepository
import com.ajay.seenu.expensetracker.domain.model.Category
import com.ajay.seenu.expensetracker.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow

class GetAllCategoriesAsFlowUseCase constructor(
    private val repository: CategoryRepository,
) {
    suspend operator fun invoke(type: TransactionType): Flow<List<Category>> {
        return repository.getCategoriesAsFlow(type)
    }
}