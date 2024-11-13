package com.ajay.seenu.expensetracker.android.domain.usecases.category

import com.ajay.seenu.expensetracker.Category
import com.ajay.seenu.expensetracker.android.data.CategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetCategoryUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(id: Long): Flow<Category> {
        return withContext(Dispatchers.IO) {
            flowOf(repository.getCategory(id))
        }
    }
}