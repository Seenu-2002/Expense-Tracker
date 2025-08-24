package com.ajay.seenu.expensetracker.data.data_source

import com.ajay.seenu.expensetracker.AttachmentEntity
import kotlinx.coroutines.flow.Flow

interface AttachmentDateSource {

    fun getAllAttachmentsForTransactionAsFlow(id: Long): Flow<List<AttachmentEntity>>
    fun getAllAttachmentsForTransaction(id: Long): List<AttachmentEntity>
    fun insertAttachment(transactionId: Long,
                         name: String,
                         filePath: String,
                         fileType: String,
                         size: Long,
                         imageUri: String)
    fun deleteAttachmentById(id: Long)
    fun deleteAttachmentsByTransactionId(id: Long)
}