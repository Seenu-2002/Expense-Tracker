package com.ajay.seenu.expensetracker.domain.model

data class Attachment constructor(
    val id: Long,
    val transactionId: Long,
    val name: String,
    val filePath: String,
    val fileType: String,
    val size: Long,
    val imageUri: String
)