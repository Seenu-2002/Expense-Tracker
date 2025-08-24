package com.ajay.seenu.expensetracker.data.data_source.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.ajay.seenu.expensetracker.CategoryEntity
import com.ajay.seenu.expensetracker.ExpenseDatabase
import com.ajay.seenu.expensetracker.data.data_source.CategoryDataSource
import com.ajay.seenu.expensetracker.data.model.TransactionTypeEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow

class CategoryLocalDataSource(database: ExpenseDatabase) : CategoryDataSource {

    private val queries = database.expenseDatabaseQueries

    override fun searchCategory(label: String, type: TransactionTypeEntity): List<CategoryEntity> {
        return queries.searchCategory(label, type).executeAsList()
    }

    override fun addCategory(label: String, type: TransactionTypeEntity, drawableRes: Long, color: Long) {
        queries.addCategory(label, type, drawableRes, color)
    }

    override fun getCategory(id: Long): CategoryEntity {
        return queries.getCategory(id).executeAsOne()
    }

    override fun getAllCategories(): List<CategoryEntity> {
        return queries.getAllCategories().executeAsList()
    }

    override fun getCategories(type: TransactionTypeEntity): List<CategoryEntity> {
        return queries.getCategories(type).executeAsList()
    }

    override fun getCategoriesAsFlow(type: TransactionTypeEntity): Flow<List<CategoryEntity>> {
        return queries.getCategories(type).asFlow().mapToList(Dispatchers.IO)
    }

    override fun deleteCategory(id: Long) {
        queries.deleteCategory(id)
    }

    override fun updateCategory(id: Long, label: String, res: Int, color: Long) {
        queries.updateCategory(label, res.toLong(), color, id)
    }
}