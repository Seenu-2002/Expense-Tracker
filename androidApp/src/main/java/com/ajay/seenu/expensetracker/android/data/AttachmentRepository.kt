package com.ajay.seenu.expensetracker.android.data

import com.ajay.seenu.expensetracker.Attachment
import com.ajay.seenu.expensetracker.AttachmentDateSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AttachmentRepository @Inject constructor(private val dataSource: AttachmentDateSource) {
    suspend fun getAllAttachmentsForTransaction(id: Long): List<Attachment> {
        return withContext(Dispatchers.IO) {
            dataSource.getAllAttachmentsForTransaction(id)
        }
    }

    suspend fun insertAttachment(transactionId: Long,
                                 name: String,
                                 filePath: String,
                                 fileType: String,
                                 size: Long): Boolean {
        withContext(Dispatchers.IO) {
            val currentAttachments = dataSource.getAllAttachmentsForTransaction(transactionId)
            if(currentAttachments.size < 5) {
                dataSource.insertAttachment(
                    transactionId = transactionId,
                    name = name,
                    filePath = filePath,
                    fileType = fileType,
                    size = size
                )
                true
            } else
                false
        }
        return false
    }

    suspend fun deleteAttachmentById(id: Long) {
        withContext(Dispatchers.IO) {
            dataSource.deleteAttachmentById(id)
        }
    }

    suspend fun deleteAttachmentsByTransactionId(id: Long) {
        withContext(Dispatchers.IO) {
            dataSource.deleteAttachmentsByTransactionId(id)
        }
    }
}