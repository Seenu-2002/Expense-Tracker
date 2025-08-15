package com.ajay.seenu.expensetracker

class AttachmentDataSourceImpl(
    private val database: ExpenseDatabase
): AttachmentDateSource {
    private val queries = database.expenseDatabaseQueries

    override fun getAllAttachmentsForTransaction(id: Long): List<Attachment> {
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