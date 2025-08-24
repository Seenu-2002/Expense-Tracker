package com.ajay.seenu.expensetracker.data.data_source

import com.ajay.seenu.expensetracker.CategoryEntity
import com.ajay.seenu.expensetracker.data.model.TransactionTypeEntity
import kotlinx.coroutines.flow.Flow

interface CategoryDataSource {
    fun searchCategory(label: String, type: TransactionTypeEntity): List<CategoryEntity>
    fun addCategory(label: String, type: TransactionTypeEntity, drawableRes: Long, color: Long)
    fun getCategory(id: Long): CategoryEntity
    fun getAllCategories(): List<CategoryEntity>
    fun getCategories(type: TransactionTypeEntity): List<CategoryEntity>
    fun getCategoriesAsFlow(type: TransactionTypeEntity): Flow<List<CategoryEntity>>
    fun deleteCategory(id: Long)
    fun updateCategory(id: Long, label: String, res: Int, color: Long)
}