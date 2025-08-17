package com.ajay.seenu.expensetracker.android.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.ajay.seenu.expensetracker.Category
import com.ajay.seenu.expensetracker.CategoryDataSource
import com.ajay.seenu.expensetracker.entity.TransactionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CategoryRepository @Inject constructor(private val dataSource: CategoryDataSource) {
    suspend fun getAllCategories(): List<Category> {
        return withContext(Dispatchers.IO) {
            dataSource.getAllCategories()
        }
    }

    suspend fun searchCategories(label: String, type: TransactionType): List<Category> {
        return withContext(Dispatchers.IO) {
            dataSource.searchCategory(label, type)
        }
    }

    suspend fun getCategories(type: TransactionType): List<Category> {
        return withContext(Dispatchers.IO) {
            dataSource.getCategories(type)
        }
    }

    suspend fun getCategoriesAsFlow(type: TransactionType): Flow<List<Category>> {
        return withContext(Dispatchers.IO) {
            dataSource.getCategoriesAsFlow(type)
        }
    }

    suspend fun addCategory(category: Category) {
        return withContext(Dispatchers.IO) {
            dataSource.addCategory(category.label, category.type, category.iconRes, category.color)
        }
    }

    suspend fun addCategory(label: String, type: TransactionType, drawableRes: Int, color: Color) {
        return withContext(Dispatchers.IO) {
            dataSource.addCategory(label, type, drawableRes.toLong(), color.toArgb().toLong())
        }
    }

    suspend fun deleteCategory(id: Long) {
        withContext(Dispatchers.IO) {
            dataSource.deleteCategory(id)
        }
    }

    suspend fun getCategory(id: Long): Category {
        return withContext(Dispatchers.IO) {
            dataSource.getCategory(id)
        }
    }

    suspend fun updateCategory(id: Long, label: String, res: Int, color: Long) {
        return withContext(Dispatchers.IO) {
            dataSource.updateCategory(id, label, res, color)
        }
    }
}