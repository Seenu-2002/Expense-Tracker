package com.ajay.seenu.expensetracker.android.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.data.repository.AccountRepository
import com.ajay.seenu.expensetracker.android.service.BudgetMonitorService
import com.ajay.seenu.expensetracker.data.repository.CategoryRepository
import com.ajay.seenu.expensetracker.domain.model.Account
import com.ajay.seenu.expensetracker.domain.model.Attachment
import com.ajay.seenu.expensetracker.domain.model.Category
import com.ajay.seenu.expensetracker.domain.model.DateFilter
import com.ajay.seenu.expensetracker.domain.model.Transaction
import com.ajay.seenu.expensetracker.domain.model.TransactionType
import com.ajay.seenu.expensetracker.domain.usecase.DateRangeCalculatorUseCase
import com.ajay.seenu.expensetracker.domain.usecase.attachment.AddAttachmentUseCase
import com.ajay.seenu.expensetracker.domain.usecase.attachment.GetAttachmentsUseCase
import com.ajay.seenu.expensetracker.domain.usecase.attachment.ReplaceAttachmentsUseCase
import com.ajay.seenu.expensetracker.domain.usecase.transaction.AddTransactionUseCase
import com.ajay.seenu.expensetracker.domain.usecase.transaction.GetTransactionUseCase
import com.ajay.seenu.expensetracker.domain.usecase.transaction.UpdateTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val addTransactionUseCase: AddTransactionUseCase,
    private val accountsRepository: AccountRepository,
    private val updateTransactionUseCase: UpdateTransactionUseCase,
    private val budgetMonitorService: BudgetMonitorService
) : ViewModel() {

    @Inject
    internal lateinit var getTransactionUseCase: GetTransactionUseCase

    @Inject
    internal lateinit var getAttachmentsUseCase: GetAttachmentsUseCase

    @Inject
    internal lateinit var addAttachmentUseCase: AddAttachmentUseCase

    @Inject
    internal lateinit var replaceAttachmentsUseCase: ReplaceAttachmentsUseCase

    @Inject
    internal lateinit var dateRangeCalculatorUseCase: DateRangeCalculatorUseCase

    private val _events = MutableSharedFlow<AddTransactionEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<AddTransactionEvent> = _events.asSharedFlow()

    private val _transaction: MutableStateFlow<Transaction?> = MutableStateFlow(null)
    val transaction = _transaction.asStateFlow()

    private var _attachments: MutableStateFlow<List<Attachment>> = MutableStateFlow(emptyList())
    val attachments: StateFlow<List<Attachment>> = _attachments.asStateFlow()

    private val _categories: MutableStateFlow<List<Category>> =
        MutableStateFlow(emptyList())
    val categories = _categories.asStateFlow()

    private val _accounts: MutableStateFlow<List<Account>> = MutableStateFlow(emptyList())
    val accounts: StateFlow<List<Account>> = _accounts.asStateFlow()

    fun init(type: TransactionType) {
        getCategories(type)
        getAccounts()
    }

    fun addTransaction(
        transaction: Transaction,
        attachments: List<Attachment>,
        filter: DateFilter
    ) {
        viewModelScope.launch {
            val range = dateRangeCalculatorUseCase(filter)
            try {
                val transactionId = addTransactionUseCase.addTransaction(transaction)
                attachments.forEach { attachment ->
                    addAttachmentUseCase.invoke(
                        transactionId = transactionId,
                        name = attachment.name,
                        fileType = attachment.fileType,
                        filePath = attachment.filePath,
                        size = attachment.size,
                        imageUri = attachment.imageUri
                    )
                }
                _events.emit(AddTransactionEvent.TransactionSaved)
                launch {
                    budgetMonitorService.checkBudgetExceeded(
                        transactionAmount = transaction.amount,
                        categoryId = transaction.category.id,
                        range = range
                    )
                }
            } catch (exp: Exception) {
                Timber.e(exp, "Error adding transaction")
                _events.emit(AddTransactionEvent.Error(exp.message ?: "Failed to save transaction"))
            }
        }
    }

    fun updateTransaction(
        transaction: Transaction,
        attachments: List<Attachment>
    ) {
        viewModelScope.launch {
            try {
                val transactionId = updateTransactionUseCase.invoke(transaction)
                replaceAttachmentsUseCase.invoke(transactionId, attachments)
                _events.emit(AddTransactionEvent.TransactionSaved)
            } catch (exp: Exception) {
                Timber.e(exp, "Error updating transaction")
                _events.emit(AddTransactionEvent.Error(exp.message ?: "Failed to update transaction"))
            }
        }
    }

    sealed class AddTransactionEvent {
        data object TransactionSaved : AddTransactionEvent()
        data class Error(val message: String) : AddTransactionEvent()
    }

    fun getTransaction(id: Long) {
        viewModelScope.launch {
            launch {
                getAttachmentsUseCase.invoke(id).collectLatest {
                    _attachments.emit(it)
                }
            }
            launch {
                getTransactionUseCase.invoke(id).collectLatest {
                    _transaction.emit(it)
                }
            }
        }
    }

    fun getCategories(type: TransactionType) {
        viewModelScope.launch {
            val categories = categoryRepository.getCategories(type)
            _categories.emit(categories)
        }
    }

    fun getAccounts() {
        viewModelScope.launch {
            val accounts = accountsRepository.getAllAccounts()
            _accounts.emit(accounts)
        }
    }

}