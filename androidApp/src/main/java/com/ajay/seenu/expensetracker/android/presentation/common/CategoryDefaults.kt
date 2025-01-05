package com.ajay.seenu.expensetracker.android.presentation.common

import android.content.Context
import androidx.compose.ui.graphics.Color
import com.ajay.seenu.expensetracker.android.R
import com.ajay.seenu.expensetracker.android.domain.data.Transaction


object CategoryDefaults {
    val categoryIconsList by lazy {
        listOf(
            R.drawable.account_balance,
            R.drawable.account_balance_wallet,
            R.drawable.attach_money,
            R.drawable.beach_access,
            R.drawable.blender,
            R.drawable.bolt,
            R.drawable.business,
            R.drawable.check_circle,
            R.drawable.commute,
            R.drawable.directions_bus,
            R.drawable.edit_note,
            R.drawable.ev_station,
            R.drawable.fastfood,
            R.drawable.flight_takeoff,
            R.drawable.home,
            R.drawable.insights,
            R.drawable.king_bed,
            R.drawable.local_cafe,
            R.drawable.local_gas_station,
            R.drawable.local_hospital,
            R.drawable.local_taxi,
            R.drawable.luggage,
            R.drawable.medical_services,
            R.drawable.menu_book,
            R.drawable.monetization_on,
            R.drawable.music_note,
            R.drawable.receipt_long,
            R.drawable.redeem,
            R.drawable.restaurant,
            R.drawable.router,
            R.drawable.savings,
            R.drawable.savings_24dp_e8eaed__1_,
            R.drawable.school,
            R.drawable.sell,
            R.drawable.shopping_bag,
            R.drawable.shopping_cart,
            R.drawable.sports_esports,
            R.drawable.star,
            R.drawable.store,
            R.drawable.theater_comedy,
            R.drawable.theaters,
            R.drawable.trending_up,
            R.drawable.two_wheeler,
            R.drawable.volunteer_activism,
            R.drawable.water_drop,
            R.drawable.wifi,
            R.drawable.wine_bar,
            R.drawable.work,
        )
    }

    val categoryColors = listOf(
        // Food & Dining
        Color(0xFFFF5722), // Vibrant Orange-Red
        Color(0xFF4CAF50), // Rich Green

        // Transportation
        Color(0xFF03A9F4), // Vibrant Blue
        Color(0xFF616161), // Bold Gray

        // Shopping
        Color(0xFFE91E63), // Hot Pink
        Color(0xFF9C27B0), // Deep Purple

        // Groceries
        Color(0xFF8BC34A), // Vibrant Light Green
        Color(0xFFFFC107), // Vibrant Yellow

        // Health & Fitness
        Color(0xFFF44336), // Vibrant Red
        Color(0xFF009688), // Teal Green

        // Entertainment
        Color(0xFF673AB7), // Bright Indigo
        Color(0xFFFF9800), // Vibrant Orange

        // Utilities
        Color(0xFF2196F3), // Bold Blue
        Color(0xFF455A64), // Dark Gray-Blue

        // Travel
        Color(0xFF00BCD4), // Cyan
        Color(0xFFF9A825), // Golden Yellow

        // Education
        Color(0xFF3F51B5), // Indigo
        Color(0xFFFFC400), // Bright Yellow

        // Miscellaneous
        Color(0xFF795548), // Rich Brown
        Color(0xFF607D8B)  // Muted Teal-Gray
    )

    fun getDefaultCategories(context: Context): List<Transaction.Category> {
        // Default Income Categories
        val incomeCategories = listOf(
            Transaction.Category(
                id = -1L,
                type = Transaction.Type.INCOME,
                label = context.getString(R.string.default_category_salary),
                color = Color(0xFF4CAF50), // Green
                res = R.drawable.monetization_on
            ),
            Transaction.Category(
                id = -1L,
                type = Transaction.Type.INCOME,
                label = context.getString(R.string.default_category_business),
                color = Color(0xFF03A9F4), // Blue
                res = R.drawable.business
            ),
            Transaction.Category(
                id = -1L,
                type = Transaction.Type.INCOME,
                label = context.getString(R.string.default_category_investments),
                color = Color(0xFFE91E63), // Pink
                res = R.drawable.trending_up
            ),
            Transaction.Category(
                id = -1L,
                type = Transaction.Type.INCOME,
                label = context.getString(R.string.default_category_freelance),
                color = Color(0xFFFF9800), // Orange
                res = R.drawable.work
            ),
            Transaction.Category(
                id = -1L,
                type = Transaction.Type.INCOME,
                label = context.getString(R.string.default_category_gifts),
                color = Color(0xFFFFC107), // Yellow
                res = R.drawable.redeem
            ),
            Transaction.Category(
                id = -1L,
                type = Transaction.Type.INCOME,
                label = context.getString(R.string.default_category_others),
                color = Color(0xFF607D8B), // Teal-Gray
                res = R.drawable.star
            )
        )

        // Default Expense Categories
        val expenseCategories = listOf(
            Transaction.Category(
                id = -1L,
                type = Transaction.Type.EXPENSE,
                label = context.getString(R.string.default_category_housing),
                color = Color(0xFF795548), // Brown
                res = R.drawable.home
            ),
            Transaction.Category(
                id = -1L,
                type = Transaction.Type.EXPENSE,
                label = context.getString(R.string.default_category_food),
                color = Color(0xFFFF5722), // Orange-Red
                res = R.drawable.fastfood
            ),
            Transaction.Category(
                id = -1L,
                type = Transaction.Type.EXPENSE,
                label = context.getString(R.string.default_category_transportation),
                color = Color(0xFF03A9F4), // Blue
                res = R.drawable.directions_bus
            ),
            Transaction.Category(
                id = -1L,
                type = Transaction.Type.EXPENSE,
                label = context.getString(R.string.default_category_health),
                color = Color(0xFFF44336), // Red
                res = R.drawable.medical_services
            ),
            Transaction.Category(
                id = -1L,
                type = Transaction.Type.EXPENSE,
                label = context.getString(R.string.default_category_education),
                color = Color(0xFF3F51B5), // Indigo
                res = R.drawable.school
            ),
            Transaction.Category(
                id = -1L,
                type = Transaction.Type.EXPENSE,
                label = context.getString(R.string.default_category_entertainment),
                color = Color(0xFF673AB7), // Indigo
                res = R.drawable.theaters
            ),
            Transaction.Category(
                id = -1L,
                type = Transaction.Type.EXPENSE,
                label = context.getString(R.string.default_category_personal_care),
                color = Color(0xFFFF9800), // Orange
                res = R.drawable.local_cafe
            ),
            Transaction.Category(
                id = -1L,
                type = Transaction.Type.EXPENSE,
                label = context.getString(R.string.default_category_savings_investments),
                color = Color(0xFF4CAF50), // Green
                res = R.drawable.savings
            ),
            Transaction.Category(
                id = -1L,
                type = Transaction.Type.EXPENSE,
                label = context.getString(R.string.default_category_debt_repayment),
                color = Color(0xFF455A64), // Gray-Blue
                res = R.drawable.receipt_long
            ),
            Transaction.Category(
                id = -1L,
                type = Transaction.Type.EXPENSE,
                label = context.getString(R.string.default_category_others),
                color = Color(0xFF607D8B), // Teal-Gray
                res = R.drawable.star
            )
        )

        return incomeCategories + expenseCategories
    }

}