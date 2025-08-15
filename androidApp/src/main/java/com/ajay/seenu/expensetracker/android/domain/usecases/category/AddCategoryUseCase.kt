package com.ajay.seenu.expensetracker.android.domain.usecases.category

import androidx.compose.ui.graphics.Color
import com.ajay.seenu.expensetracker.android.data.CategoryRepository
import com.ajay.seenu.expensetracker.android.domain.data.Transaction
import com.ajay.seenu.expensetracker.android.domain.mapper.CategoryMapper.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddCategoryUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(
        label: String,
        type: Transaction.Type,
        res: Int,
        color: Color
    ) {
        withContext(Dispatchers.IO) {
            val searchCategories = repository.searchCategories(label, type.map())
            if (searchCategories.isNotEmpty()) {
                throw IllegalStateException("Category $label already present")
            }
            repository.addCategory(label, type.map(), res, color)
        }
    }
}