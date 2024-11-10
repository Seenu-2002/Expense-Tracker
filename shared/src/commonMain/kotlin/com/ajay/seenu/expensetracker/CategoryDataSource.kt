package com.ajay.seenu.expensetracker

import com.ajay.seenu.expensetracker.entity.TransactionType

interface CategoryDataSource {
    fun addCategory(label: String, type: TransactionType, parentId: Long?)
    fun getCategory(id: Long): Category
    fun getAllCategories(): List<Category>
    fun deleteCategory(id: Long)
    fun updateCategory(id: Long, label: String, type: TransactionType)
}