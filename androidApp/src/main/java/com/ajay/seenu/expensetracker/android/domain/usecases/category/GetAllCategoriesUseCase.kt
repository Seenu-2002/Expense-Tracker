package com.ajay.seenu.expensetracker.android.domain.usecases.category

import com.ajay.seenu.expensetracker.android.data.CategoryRepository
import com.ajay.seenu.expensetracker.android.domain.data.Transaction
import com.ajay.seenu.expensetracker.android.domain.mapper.CategoryMapper
import com.ajay.seenu.expensetracker.android.domain.mapper.CategoryMapper.map
import javax.inject.Inject

class GetAllCategoriesUseCase @Inject constructor(
    private val repository: CategoryRepository,
) {
    suspend fun invoke(type: Transaction.Type): List<Transaction.Category> {
        val data = repository.getCategories(type.map())
        return CategoryMapper.mapCategories(data)
    }
}