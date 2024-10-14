package com.ajay.seenu.expensetracker.android.domain.usecases.attachment

import com.ajay.seenu.expensetracker.Attachment
import com.ajay.seenu.expensetracker.android.data.AttachmentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetAttachmentsUseCase @Inject constructor(
    private val repository: AttachmentRepository
) {
    suspend operator fun invoke(id: Long): Flow<List<Attachment>> {
        return withContext(Dispatchers.IO) {
            flowOf(repository.getAllAttachmentsForTransaction(id))
        }
    }
}