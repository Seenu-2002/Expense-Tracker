package com.ajay.seenu.expensetracker.android.domain.usecases.attachment

import com.ajay.seenu.expensetracker.android.data.AttachmentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddAttachmentUseCase@Inject constructor(
    private val repository: AttachmentRepository
) {
    suspend operator fun invoke(transactionId: Long,
                                name: String,
                                filePath: String,
                                fileType: String,
                                size: Long) {
        withContext(Dispatchers.IO) {
            repository.insertAttachment(
                transactionId = transactionId,
                name = name,
                filePath = filePath,
                fileType = fileType,
                size = size
            )
        }
    }
}