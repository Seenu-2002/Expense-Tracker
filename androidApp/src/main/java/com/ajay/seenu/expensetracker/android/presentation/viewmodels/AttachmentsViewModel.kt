package com.ajay.seenu.expensetracker.android.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.domain.usecase.attachment.AddAttachmentUseCase
import com.ajay.seenu.expensetracker.domain.model.Attachment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttachmentsViewModel @Inject constructor(): ViewModel() {
    private var _attachments: MutableStateFlow<List<Attachment>> = MutableStateFlow(emptyList())
    val attachments: StateFlow<List<Attachment>> = _attachments.asStateFlow()

    @Inject
    internal lateinit var addAttachmentUseCase: AddAttachmentUseCase

    fun getAttachmentsForTransaction(id: Long) {

    }

    fun insertAttachment(transactionId: Long,
                         name: String,
                         filePath: String,
                         fileType: String,
                         size: Long,
                         imageUri: String) {
        viewModelScope.launch {
            addAttachmentUseCase.invoke(
                transactionId = transactionId,
                name = name,
                filePath = filePath,
                fileType = fileType,
                size = size,
                imageUri = imageUri
            )
        }
    }
}