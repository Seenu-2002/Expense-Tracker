package com.ajay.seenu.expensetracker

interface AttachmentDateSource {
    fun getAllAttachmentsForTransaction(id: Long): List<Attachment>
    fun insertAttachment(transactionId: Long,
                         name: String,
                         filePath: String,
                         fileType: String,
                         size: Long,
                         imageUri: String)
    fun deleteAttachmentById(id: Long)
    fun deleteAttachmentsByTransactionId(id: Long)
}