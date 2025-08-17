package com.ajay.seenu.expensetracker

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.ajay.seenu.expensetracker.entity.TransactionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow

class CategoryDataSourceImpl(database: ExpenseDatabase) : CategoryDataSource {

    private val queries = database.expenseDatabaseQueries

    override fun searchCategory(label: String, type: TransactionType): List<Category> {
        return queries.searchCategory(label, type).executeAsList()
    }

    override fun addCategory(label: String, type: TransactionType, drawableRes: Long, color: Long) {
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

    override fun getCategoriesAsFlow(type: TransactionType): Flow<List<Category>> {
        return queries.getCategories(type).asFlow().mapToList(Dispatchers.IO)
    }

    override fun deleteCategory(id: Long) {
        return queries.deleteCategory(id)
    }

    override fun updateCategory(id: Long, label: String, res: Int, color: Long) {
        return queries.updateCategory(label, res.toLong(), color, id)
    }
}