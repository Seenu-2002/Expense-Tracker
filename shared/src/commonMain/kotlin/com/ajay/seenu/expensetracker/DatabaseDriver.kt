package com.ajay.seenu.expensetracker

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import kotlinx.datetime.LocalDate

const val DATABASE_NAME = "expenseDatabase.db"

expect class DriverFactory {
    fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: DriverFactory): ExpenseDatabase {
    val driver = driverFactory.createDriver()
    driver.execute(
        identifier = null,
        sql = "PRAGMA foreign_keys=ON",
        parameters = 0
    )
    return ExpenseDatabase(
        driver,
        CategoryAdapter = Category.Adapter(EnumColumnAdapter()),
        TransactionDetailAdapter = TransactionDetail.Adapter(
            typeAdapter = EnumColumnAdapter(),
            paymentTypeAdapter = EnumColumnAdapter()
        )
    )
}