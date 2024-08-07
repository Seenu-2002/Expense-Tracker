package com.ajay.seenu.expensetracker

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(ExpenseDatabase.Schema, DATABASE_NAME)
    }
}