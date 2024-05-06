package com.ajay.seenu.expensetracker.android.domain.mapper

import com.ajay.seenu.expensetracker.Category
import com.ajay.seenu.expensetracker.android.domain.data.Transaction
import com.ajay.seenu.expensetracker.android.domain.mapper.CategoryMapper.map
import com.ajay.seenu.expensetracker.entity.TransactionType

object CategoryMapper {

    fun mapCategories(categories: List<Category>): List<Transaction.Category> {
        val mappedCategories = arrayListOf<Transaction.Category>()
        val parentIdVsCategory = hashMapOf<Long, Transaction.Category>()
        categories.forEach {
            val category = Transaction.Category(
                it.id,
                it.type.map(),
                it.label,
                null
            )

            if (it.parent != null) {
                parentIdVsCategory[it.parent!!] = category
            }

            mappedCategories.add(category)
        }
        for(entry in parentIdVsCategory.entries) {
            val parentId = entry.key
            val category = entry.value
            val parent = mappedCategories.find { it.id == parentId } ?: continue
            category.parent = parent
        }
        parentIdVsCategory.clear()
        return mappedCategories
    }

    fun TransactionType.map(): Transaction.Type {
        return when (this) {
            TransactionType.INCOME -> Transaction.Type.INCOME
            TransactionType.EXPENSE -> Transaction.Type.EXPENSE
        }
    }

    fun Transaction.Category.map(): Category {
        return Category(
            this.id,
            this.label,
            this.type.map(),
            this.parent?.id,
        )
    }

    fun Transaction.Type.map(): TransactionType {
        return when (this) {
            Transaction.Type.EXPENSE -> TransactionType.EXPENSE
            Transaction.Type.INCOME -> TransactionType.INCOME
        }
    }
}