package com.ajay.seenu.expensetracker

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

actual class DriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = ExpenseDatabase.Schema,
            context = context,
            name = DATABASE_NAME
        )
    }
}