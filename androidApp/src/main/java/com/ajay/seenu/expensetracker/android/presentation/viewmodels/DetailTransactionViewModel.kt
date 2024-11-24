package com.ajay.seenu.expensetracker.android.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.Attachment
import com.ajay.seenu.expensetracker.android.data.TransactionRepository
import com.ajay.seenu.expensetracker.android.domain.data.Transaction
import com.ajay.seenu.expensetracker.android.domain.usecases.attachment.GetAttachmentsUseCase
import com.ajay.seenu.expensetracker.android.domain.usecases.transaction.GetTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailTransactionViewModel @Inject constructor(
    private val repository: TransactionRepository,
    private val getTransactionUseCase: GetTransactionUseCase,
    private val getAttachmentsUseCase: GetAttachmentsUseCase
): ViewModel() {

    private val _transaction: MutableStateFlow<Transaction?> = MutableStateFlow(null)
    val transaction = _transaction.asStateFlow()

    private var _attachments: MutableStateFlow<List<Attachment>> = MutableStateFlow(emptyList())
    val attachments: StateFlow<List<Attachment>> = _attachments.asStateFlow()

    fun getTransaction(id: Long) {
        viewModelScope.launch {
            getTransactionUseCase.invoke(id).collectLatest {
                _transaction.emit(it)
                getAttachments(id)
            }
        }
    }

    private fun getAttachments(id: Long) {
        viewModelScope.launch {
            getAttachmentsUseCase.invoke(id).collectLatest {
                _attachments.emit(it)
            }
        }
    }

}