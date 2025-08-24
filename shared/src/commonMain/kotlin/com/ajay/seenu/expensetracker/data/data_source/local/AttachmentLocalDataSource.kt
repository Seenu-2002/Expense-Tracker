package com.ajay.seenu.expensetracker.data.data_source.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.ajay.seenu.expensetracker.AttachmentEntity
import com.ajay.seenu.expensetracker.ExpenseDatabase
import com.ajay.seenu.expensetracker.data.data_source.AttachmentDateSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow

class AttachmentLocalDataSource(
    private val database: ExpenseDatabase
): AttachmentDateSource {
    private val queries = database.expenseDatabaseQueries
    override fun getAllAttachmentsForTransactionAsFlow(id: Long): Flow<List<AttachmentEntity>> {
        return queries.getAllAttachmentsForTransaction(id)
            .asFlow()
            .mapToList(Dispatchers.IO)
    }
    override fun getAllAttachmentsForTransaction(id: Long): List<AttachmentEntity> {
        return queries.getAllAttachmentsForTransaction(id).executeAsList()
    }

    override fun insertAttachment(
        transactionId: Long,
        name: String,
        filePath: String,
        fileType: String,
        size: Long,
        imageUri: String
    ) {
        queries.insertAttachment(
            transactionId = transactionId,
            name = name,
            filePath = filePath,
            fileType = fileType,
            size = size,
            imageUri = imageUri
        )
    }

    override fun deleteAttachmentById(id: Long) {
        queries.deleteAttachmentById(id)
    }

    override fun deleteAttachmentsByTransactionId(id: Long) {
        queries.deleteAttachmentsByTransactionId(id)
    }
}