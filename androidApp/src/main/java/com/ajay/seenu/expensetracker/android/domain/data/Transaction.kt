package com.ajay.seenu.expensetracker.android.domain.data

import androidx.annotation.StringRes
import com.ajay.seenu.expensetracker.Category
import com.ajay.seenu.expensetracker.TransactionDetail
import com.ajay.seenu.expensetracker.android.R
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

    enum class Type(val value: String, @StringRes val resourceId: Int) {
        INCOME("Income", R.drawable.income), EXPENSE("Expense", R.drawable.expense)
    }

    data class Category constructor(
        val id: Long,
        val type: Type,
        val label: String,
        var parent: Category?
    )
}

