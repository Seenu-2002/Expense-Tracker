package com.ajay.seenu.expensetracker

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform