package com.ajay.seenu.expensetracker.data.repository

import com.ajay.seenu.expensetracker.AttachmentEntity
import com.ajay.seenu.expensetracker.data.data_source.AttachmentDateSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class AttachmentRepository constructor(
    private val localDataSource: AttachmentDateSource
) {

    suspend fun getAllAttachmentsForTransactionAsFlow(id: Long): Flow<List<AttachmentEntity>> {
        return withContext(Dispatchers.IO) {
            localDataSource.getAllAttachmentsForTransactionAsFlow(id)
        }
    }
    
    suspend fun getAllAttachmentsForTransaction(id: Long): List<AttachmentEntity> {
        return withContext(Dispatchers.IO) {
            localDataSource.getAllAttachmentsForTransaction(id)
        }
    }

    suspend fun insertAttachment(
        transactionId: Long,
        name: String,
        filePath: String,
        fileType: String,
        size: Long,
        imageUri: String
    ): Boolean {
        withContext(Dispatchers.IO) {
            val currentAttachments = localDataSource.getAllAttachmentsForTransaction(transactionId)
            if (currentAttachments.size < 5) {
                localDataSource.insertAttachment(
                    transactionId = transactionId,
                    name = name,
                    filePath = filePath,
                    fileType = fileType,
                    size = size,
                    imageUri = imageUri
                )
                true
            } else
                false
        }
        return false
    }

    suspend fun deleteAttachmentById(id: Long) {
        withContext(Dispatchers.IO) {
            localDataSource.deleteAttachmentById(id)
        }
    }

    suspend fun deleteAttachmentsByTransactionId(id: Long) {
        withContext(Dispatchers.IO) {
            localDataSource.deleteAttachmentsByTransactionId(id)
        }
    }

}