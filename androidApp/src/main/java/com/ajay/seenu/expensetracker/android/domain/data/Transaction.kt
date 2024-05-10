package com.ajay.seenu.expensetracker.android.domain.data

import com.ajay.seenu.expensetracker.Category
import com.ajay.seenu.expensetracker.TransactionDetail
import com.ajay.seenu.expensetracker.entity.PaymentType
import java.util.Date

data class Transaction constructor(
    val id: Long,
    val type: Type,
    val amount: Double,
    val category: Category,
    val paymentType: PaymentType,
    val date: Date,
    val note: String? = null
) {

    enum class Type(val value: String) {
        INCOME("Income"), EXPENSE("Expense")
    }

    data class Category(
        val id: Long,
        val type: Type,
        val label: String,
        var parent: Category?,
    )
}

