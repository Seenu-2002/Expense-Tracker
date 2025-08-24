package com.ajay.seenu.expensetracker.data.mapper

import com.ajay.seenu.expensetracker.AttachmentEntity
import com.ajay.seenu.expensetracker.domain.model.Attachment

fun AttachmentEntity.toDomain(): Attachment {
    return Attachment(
        id = id,
        transactionId = transactionId,
        name = name,
        filePath = filePath,
        fileType = fileType,
        size = size,
        imageUri = imageUri
    )
}

fun Attachment.toEntity(): AttachmentEntity {
    return AttachmentEntity(
        id = id,
        transactionId = transactionId,
        name = name,
        filePath = filePath,
        fileType = fileType,
        size = size,
        imageUri = imageUri
    )
}
