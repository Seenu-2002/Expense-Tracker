package com.ajay.seenu.expensetracker.android.presentation.viewmodels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.android.data.TransactionRepository
import com.ajay.seenu.expensetracker.android.domain.data.Transaction
import com.ajay.seenu.expensetracker.android.domain.mapper.CategoryMapper
import com.ajay.seenu.expensetracker.android.domain.usecases.attachment.AddAttachmentUseCase
import com.ajay.seenu.expensetracker.android.domain.usecases.transaction.AddTransactionUseCase
import com.ajay.seenu.expensetracker.android.domain.usecases.transaction.GetTransactionUseCase
import com.ajay.seenu.expensetracker.android.domain.util.getFileInfoFromUri
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val repository: TransactionRepository,
    private val addTransactionUseCase: AddTransactionUseCase
) : ViewModel() {

    @Inject
    internal lateinit var getTransactionUseCase: GetTransactionUseCase

    @Inject
    internal lateinit var addAttachmentUseCase: AddAttachmentUseCase

    private val _transaction: MutableStateFlow<Transaction?> = MutableStateFlow(null)
    val transaction = _transaction.asStateFlow()

    private val _categories: MutableStateFlow<List<Transaction.Category>> = MutableStateFlow(emptyList())
    val categories = _categories.asStateFlow()

    fun addTransaction(context: Context,
                       transaction: Transaction,
                       attachments: List<Uri>) {
        viewModelScope.launch {
            try {
                val transactionId = addTransactionUseCase.addTransaction(transaction)
                attachments.forEach { uri ->
                    val fileInfo = getFileInfoFromUri(context, uri)
                    addAttachmentUseCase.invoke(
                        transactionId = transactionId,
                        name = fileInfo["fileName"] ?: "N/A",
                        fileType = fileInfo["fileType"] ?: "N/A",
                        filePath = fileInfo["filePath"] ?: "N/A",
                        size = fileInfo["fileSize"]?.toLong() ?: 0L
                    )
                }
            } catch (exp: Exception) {
                // FIXME: Show exception
            }

        }
    }

    fun getTransaction(id: Long) {
        viewModelScope.launch {
            getTransactionUseCase.invoke(id).collectLatest {
                _transaction.emit(it)
            }
        }
    }

    fun getCategories(type: Transaction.Type) {
        viewModelScope.launch {
            val categories = repository.getCategories(type)
            _categories.emit(CategoryMapper.mapCategories(categories)) // FIXME: Introduce use case and move the mapping logic there
        }
    }

}