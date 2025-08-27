package com.ajay.seenu.expensetracker.domain.usecase.attachment

import com.ajay.seenu.expensetracker.data.mapper.toDomain
import com.ajay.seenu.expensetracker.data.repository.AttachmentRepository
import com.ajay.seenu.expensetracker.domain.model.Attachment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAttachmentsUseCase constructor(
    private val repository: AttachmentRepository
) {

    suspend operator fun invoke(id: Long): Flow<List<Attachment>> {
        return repository.getAllAttachmentsForTransactionAsFlow(id).map {
            it.map { attachmentEntity ->
                attachmentEntity.toDomain()
            }
        }
    }

}