package com.ajay.seenu.expensetracker.android.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.Category
import com.ajay.seenu.expensetracker.android.data.TransactionRepository
import com.ajay.seenu.expensetracker.android.domain.data.Transaction
import com.ajay.seenu.expensetracker.android.domain.mapper.CategoryMapper
import com.ajay.seenu.expensetracker.android.domain.usecases.AddTransactionUseCase
import com.ajay.seenu.expensetracker.android.domain.usecases.GetRecentTransactionsUseCase
import com.ajay.seenu.expensetracker.android.domain.usecases.GetTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val repository: TransactionRepository,
    private val addTransactionUseCase: AddTransactionUseCase,
    private val dateFormat: SimpleDateFormat
) : ViewModel() {

    @Inject
    internal lateinit var getTransactionUseCase: GetTransactionUseCase

    private val _transaction: MutableStateFlow<Transaction?> = MutableStateFlow(null)
    val transaction = _transaction.asStateFlow()

    private val _categories: MutableStateFlow<List<Transaction.Category>> = MutableStateFlow(emptyList())
    val categories = _categories.asStateFlow()

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                addTransactionUseCase.addTransaction(transaction)
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