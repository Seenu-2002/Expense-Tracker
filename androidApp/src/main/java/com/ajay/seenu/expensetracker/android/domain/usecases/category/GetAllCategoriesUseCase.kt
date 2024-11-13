package com.ajay.seenu.expensetracker.android.domain.usecases.category

import com.ajay.seenu.expensetracker.Category
import com.ajay.seenu.expensetracker.android.data.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import javax.inject.Inject

class GetAllCategoriesUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend fun invoke(): Flow<List<Category>> {
        val data = repository.getAllCategories()
        return listOf(data).asFlow()
    }
}