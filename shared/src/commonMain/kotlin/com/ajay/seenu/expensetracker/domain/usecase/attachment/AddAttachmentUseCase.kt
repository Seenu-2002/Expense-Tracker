package com.ajay.seenu.expensetracker.domain.usecase.attachment

import com.ajay.seenu.expensetracker.data.repository.AttachmentRepository

class AddAttachmentUseCase constructor(
    private val repository: AttachmentRepository
) {

    suspend operator fun invoke(
        transactionId: Long,
        name: String,
        filePath: String,
        fileType: String,
        size: Long,
        imageUri: String
    ) {
        repository.insertAttachment(
            transactionId = transactionId,
            name = name,
            filePath = filePath,
            fileType = fileType,
            size = size,
            imageUri = imageUri
        )
    }

}