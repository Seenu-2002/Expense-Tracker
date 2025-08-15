package com.ajay.seenu.expensetracker.android.domain.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.ajay.seenu.expensetracker.android.R
import com.ajay.seenu.expensetracker.android.presentation.common.ChartDefaults
import com.ajay.seenu.expensetracker.entity.PaymentType
import kotlinx.serialization.Serializable
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

    @Serializable
    enum class Type(
        val value: String,
        @StringRes val stringRes: Int,
        @DrawableRes val resourceId: Int,
        @StringRes val placeHolderRes: Int
    ) {
        INCOME(
            "Income",
            R.string.income,
            R.drawable.income,
            R.string.income_format),
        EXPENSE(
            "Expense",
            R.string.expense,
            R.drawable.expense,
            R.string.expense_format
        );

        fun getColor(): Color {
            return when (this) {
                EXPENSE -> ChartDefaults.expenseColor
                INCOME -> ChartDefaults.incomeColor
            }
        }
    }

    data class Category constructor(
        val id: Long,
        val type: Type,
        val label: String,
        val color: Color,
        val res: Int,
    )
}

