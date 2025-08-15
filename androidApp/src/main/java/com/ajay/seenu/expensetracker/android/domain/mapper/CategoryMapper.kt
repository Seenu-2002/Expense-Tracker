package com.ajay.seenu.expensetracker.android.domain.mapper

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.ajay.seenu.expensetracker.Category
import com.ajay.seenu.expensetracker.android.domain.data.Transaction
import com.ajay.seenu.expensetracker.entity.TransactionType

object CategoryMapper {

    fun mapCategories(categories: List<Category>): List<Transaction.Category> = categories.map {
        mapCategory(it)
    }

    fun mapCategory(category: Category): Transaction.Category = Transaction.Category(
        category.id,
        category.type.map(),
        category.label,
        Color(category.color),
        category.iconRes.toInt()
    )

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
            this.res.toLong(),
            this.color.toArgb().toLong(),
        )
    }

    fun Transaction.Type.map(): TransactionType {
        return when (this) {
            Transaction.Type.EXPENSE -> TransactionType.EXPENSE
            Transaction.Type.INCOME -> TransactionType.INCOME
        }
    }
}