package com.ajay.seenu.expensetracker

import app.cash.sqldelight.db.SqlDriver
import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

//actual class DriverFactory {
//    actual fun createDriver(): SqlDriver {
//        return NativeSqliteDriver(Database.Schema, "test.db")
//    }
//}