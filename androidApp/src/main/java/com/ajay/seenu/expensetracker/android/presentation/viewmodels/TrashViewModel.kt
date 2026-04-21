package com.ajay.seenu.expensetracker.android.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.android.presentation.state.Error
import com.ajay.seenu.expensetracker.android.presentation.state.UiState
import com.ajay.seenu.expensetracker.domain.model.Transaction
import com.ajay.seenu.expensetracker.domain.usecase.transaction.GetTrashTransactionsUseCase
import com.ajay.seenu.expensetracker.domain.usecase.transaction.PermanentlyDeleteTransactionUseCase
import com.ajay.seenu.expensetracker.domain.usecase.transaction.RestoreTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TrashViewModel @Inject constructor(
    private val getTrashTransactionsUseCase: GetTrashTransactionsUseCase,
    private val restoreTransactionUseCase: RestoreTransactionUseCase,
    private val permanentlyDeleteTransactionUseCase: PermanentlyDeleteTransactionUseCase
) : ViewModel() {

    private val _transactions: MutableStateFlow<UiState<List<Transaction>>> =
        MutableStateFlow(UiState.Loading)
    val transactions = _transactions.asStateFlow()

    init {
        loadTrash()
    }

    fun loadTrash() {
        viewModelScope.launch {
            try {
                getTrashTransactionsUseCase().collectLatest { data ->
                    _transactions.emit(
                        if (data.data.isEmpty()) UiState.Empty
                        else UiState.Success(data.data)
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading trash")
                _transactions.emit(UiState.Failure(Error.Unhandled(e)))
            }
        }
    }

    fun restoreTransaction(id: Long) {
        viewModelScope.launch {
            try {
                restoreTransactionUseCase(id)
                loadTrash()
            } catch (e: Exception) {
                Timber.e(e, "Error restoring transaction")
            }
        }
    }

    fun permanentlyDelete(id: Long) {
        viewModelScope.launch {
            try {
                permanentlyDeleteTransactionUseCase(id)
                loadTrash()
            } catch (e: Exception) {
                Timber.e(e, "Error permanently deleting transaction")
            }
        }
    }

    fun emptyTrash() {
        val current = _transactions.value
        if (current !is UiState.Success) return
        viewModelScope.launch {
            try {
                current.data.forEach { permanentlyDeleteTransactionUseCase(it.id) }
                _transactions.emit(UiState.Empty)
            } catch (e: Exception) {
                Timber.e(e, "Error emptying trash")
            }
        }
    }
}
