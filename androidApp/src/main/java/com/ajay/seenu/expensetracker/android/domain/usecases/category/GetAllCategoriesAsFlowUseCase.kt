package com.ajay.seenu.expensetracker.android.domain.usecases.category

import com.ajay.seenu.expensetracker.android.data.CategoryRepository
import com.ajay.seenu.expensetracker.android.domain.data.Transaction
import com.ajay.seenu.expensetracker.android.domain.mapper.CategoryMapper
import com.ajay.seenu.expensetracker.android.domain.mapper.CategoryMapper.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllCategoriesAsFlowUseCase @Inject constructor(
    private val repository: CategoryRepository,
) {
    suspend operator fun invoke(type: Transaction.Type): Flow<List<Transaction.Category>> {
        return repository.getCategoriesAsFlow(type.map()).map {
            CategoryMapper.mapCategories(it)
        }
    }
}