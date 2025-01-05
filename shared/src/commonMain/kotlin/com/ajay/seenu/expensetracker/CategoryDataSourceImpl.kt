package com.ajay.seenu.expensetracker

import com.ajay.seenu.expensetracker.entity.TransactionType

class CategoryDataSourceImpl(database: ExpenseDatabase): CategoryDataSource {

    private val queries = database.expenseDatabaseQueries

    override fun addCategory(label: String, type: TransactionType, drawableRes: Long?, color: Long) {
        return queries.addCategory(label, type, drawableRes, color)
    }

    override fun getCategory(id: Long): Category {
        return queries.getCategory(id).executeAsOne()
    }

    override fun getAllCategories(): List<Category> {
        return queries.getAllCategories().executeAsList()
    }

    override fun getCategories(type: TransactionType): List<Category> {
        return queries.getCategories(type).executeAsList()
    }

    override fun deleteCategory(id: Long) {
        return queries.deleteCategory(id)
    }

    override fun updateCategory(id: Long, label: String, type: TransactionType) {
        return queries.updateCategory(label, type, id)
    }
}