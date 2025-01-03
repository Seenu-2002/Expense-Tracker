package com.ajay.seenu.expensetracker.android.domain.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.ui.graphics.Color
import com.ajay.seenu.expensetracker.android.R
import com.ajay.seenu.expensetracker.android.presentation.common.ChartDefaults
import com.ajay.seenu.expensetracker.entity.PaymentType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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

    @Serializable
    data class Category constructor(
        val id: Long,
        val type: Type,
        val label: String,
        var parent: Category?
    ) {

        object Saver : androidx.compose.runtime.saveable.Saver<Category, String> {

            @OptIn(ExperimentalSerializationApi::class)
            override fun restore(value: String): Category? {
                return Json.decodeFromString(value)
            }

            @OptIn(ExperimentalSerializationApi::class)
            override fun SaverScope.save(value: Category): String? {
                return Json.encodeToString(value)
            }

        }

    }
}

