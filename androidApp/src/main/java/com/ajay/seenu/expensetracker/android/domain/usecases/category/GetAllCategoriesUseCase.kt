package com.ajay.seenu.expensetracker.android.domain.usecases.category

import com.ajay.seenu.expensetracker.android.data.TransactionRepository
import com.ajay.seenu.expensetracker.android.domain.data.Transaction
import com.ajay.seenu.expensetracker.android.domain.mapper.CategoryMapper
import javax.inject.Inject

class GetAllCategoriesUseCase @Inject constructor(
    private val repository: TransactionRepository
) {

    suspend operator fun invoke(type: Transaction.Type): List<Transaction.Category> {
        return repository.getCategories(
            type
        ).map { CategoryMapper.mapCategory(it) }
    }

}