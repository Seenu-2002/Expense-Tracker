package com.ajay.seenu.expensetracker.entity

interface FileManager {
    suspend fun saveFile(content: String, fileName: String, mimeType: String): Boolean
    suspend fun shareFile(content: String, fileName: String, mimeType: String): Boolean
    fun getExportsDirectory(): String
}