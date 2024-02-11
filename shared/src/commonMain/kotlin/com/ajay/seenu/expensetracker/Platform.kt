package com.ajay.seenu.expensetracker

import app.cash.sqldelight.db.SqlDriver

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

//expect class DriverFactory {
//    fun createDriver(): SqlDriver
//}

//fun createDatabase(driverFactory): Database {
//    val driver = driverFactory.createDriver()
//    val database = Database(driver)
//
//    // Do more work with the database (see below).
//}