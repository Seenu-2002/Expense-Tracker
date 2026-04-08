package com.ajay.seenu.expensetracker.domain.usecase.attachment

import com.ajay.seenu.expensetracker.data.data_source.AttachmentInsertParams
import com.ajay.seenu.expensetracker.data.repository.AttachmentRepository
import com.ajay.seenu.expensetracker.domain.model.Attachment

class ReplaceAttachmentsUseCase constructor(
    private val repository: AttachmentRepository
) {

    suspend operator fun invoke(
        transactionId: Long,
        attachments: List<Attachment>
    ) {
        repository.deleteAndReinsertAttachments(
            transactionId = transactionId,
            attachments = attachments.map { att ->
                AttachmentInsertParams(
                    name = att.name,
                    filePath = att.filePath,
                    fileType = att.fileType,
                    size = att.size,
                    imageUri = att.imageUri
                )
            }
        )
    }

}
