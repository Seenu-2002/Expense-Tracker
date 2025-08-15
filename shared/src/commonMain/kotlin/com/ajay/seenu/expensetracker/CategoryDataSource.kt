package com.ajay.seenu.expensetracker

import com.ajay.seenu.expensetracker.entity.TransactionType
import kotlinx.coroutines.flow.Flow

interface CategoryDataSource {
    fun searchCategory(label: String, type: TransactionType): List<Category>
    fun addCategory(label: String, type: TransactionType, drawableRes: Long, color: Long)
    fun getCategory(id: Long): Category
    fun getAllCategories(): List<Category>
    fun getCategories(type: TransactionType): Flow<List<Category>>
    fun deleteCategory(id: Long)
    fun updateCategory(id: Long, label: String, res: Int, color: Long)
}