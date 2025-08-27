package com.ajay.seenu.expensetracker.data.repository

import com.ajay.seenu.expensetracker.data.data_source.CategoryDataSource
import com.ajay.seenu.expensetracker.data.mapper.toDomain
import com.ajay.seenu.expensetracker.data.mapper.toEntity
import com.ajay.seenu.expensetracker.domain.model.Category
import com.ajay.seenu.expensetracker.domain.model.TransactionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class CategoryRepository constructor(
    private val localDataSource: CategoryDataSource
) {

    suspend fun getAllCategories(): List<Category> {
        return withContext(Dispatchers.IO) {
            localDataSource.getAllCategories().map {
                it.toDomain()
            }
        }
    }

    suspend fun searchCategories(label: String, type: TransactionType): List<Category> {
        return withContext(Dispatchers.IO) {
            localDataSource.searchCategory(label, type.toEntity()).map {
                it.toDomain()
            }
        }
    }

    suspend fun getCategories(type: TransactionType): List<Category> {
        return withContext(Dispatchers.IO) {
            localDataSource.getCategories(type.toEntity()).map { it.toDomain() }
        }
    }

    suspend fun getCategoriesAsFlow(type: TransactionType): Flow<List<Category>> {
        return withContext(Dispatchers.IO) {
            localDataSource.getCategoriesAsFlow(type.toEntity()).map { it ->
                it.map { categoryEntity ->
                    categoryEntity.toDomain()
                }
            }
        }
    }

    suspend fun addCategory(category: Category) {
        return withContext(Dispatchers.IO) {
            localDataSource.addCategory(
                category.label,
                category.type.toEntity(),
                category.iconRes.toLong(),
                category.color
            )
        }
    }

    suspend fun addCategory(label: String, type: TransactionType, drawableRes: Int, color: Long) {
        return withContext(Dispatchers.IO) {
            localDataSource.addCategory(label, type.toEntity(), drawableRes.toLong(), color)
        }
    }

    suspend fun deleteCategory(id: Long) {
        withContext(Dispatchers.IO) {
            localDataSource.deleteCategory(id)
        }
    }

    suspend fun getCategory(id: Long): Category {
        return withContext(Dispatchers.IO) {
            localDataSource.getCategory(id).toDomain()
        }
    }

    suspend fun updateCategory(id: Long, label: String, res: Int, color: Long) {
        return withContext(Dispatchers.IO) {
            localDataSource.updateCategory(id, label, res, color)
        }
    }

}