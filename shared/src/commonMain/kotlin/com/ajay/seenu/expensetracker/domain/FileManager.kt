package com.ajay.seenu.expensetracker.domain

interface FileManager {
    suspend fun saveFile(content: String, fileName: String, mimeType: String): Boolean
    suspend fun shareFile(content: String, fileName: String, mimeType: String): Boolean
    suspend fun saveBytes(data: ByteArray, fileName: String, mimeType: String): Boolean
    suspend fun shareBytes(data: ByteArray, fileName: String, mimeType: String): Boolean
    fun getExportsDirectory(): String
}