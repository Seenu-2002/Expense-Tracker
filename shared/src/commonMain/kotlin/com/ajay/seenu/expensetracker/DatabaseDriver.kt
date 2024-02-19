package com.ajay.seenu.expensetracker

import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.db.SqlDriver

const val DATABASE_NAME = "expenseDatabase.db"
expect class DriverFactory {
    fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: DriverFactory): ExpenseDatabase {
    val driver = driverFactory.createDriver()
    return ExpenseDatabase(driver,
        IncomeAdapter = Income.Adapter(
            categoryAdapter = EnumColumnAdapter(),
            paymentTypeAdapter = EnumColumnAdapter()
        ),
        ExpenseAdapter = Expense.Adapter(
            categoryAdapter = EnumColumnAdapter(),
            paymentTypeAdapter = EnumColumnAdapter()
        ))
}