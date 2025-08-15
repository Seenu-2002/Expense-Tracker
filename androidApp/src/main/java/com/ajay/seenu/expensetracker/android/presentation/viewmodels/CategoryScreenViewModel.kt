package com.ajay.seenu.expensetracker.android.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.android.domain.data.Transaction
import com.ajay.seenu.expensetracker.android.domain.data.UiState
import com.ajay.seenu.expensetracker.android.domain.usecases.category.DeleteCategoryUseCase
import com.ajay.seenu.expensetracker.android.domain.usecases.category.GetAllCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryScreenViewModel @Inject constructor(
) : ViewModel() {

    private val _categories: MutableStateFlow<UiState<List<Transaction.Category>>> = MutableStateFlow(UiState.Empty)
    val categories = _categories.asStateFlow()

    private val _category: MutableStateFlow<Transaction.Category?> = MutableStateFlow(null)
    val category = _category.asStateFlow()

    private val _transactionType: MutableStateFlow<Transaction.Type> = MutableStateFlow(Transaction.Type.INCOME)
    val transactionType: StateFlow<Transaction.Type> = _transactionType.asStateFlow()

    @Inject
    internal lateinit var getCategoriesUseCase: GetAllCategoriesUseCase

    @Inject
    internal lateinit var deleteCategoryUseCase: DeleteCategoryUseCase

    fun getCategories(type: Transaction.Type) {
        viewModelScope.launch {
            _categories.emit(UiState.Loading)
            getCategoriesUseCase(type).collectLatest {
                _categories.emit(UiState.Success(it))
            }
        }
    }

    fun deleteCategory(id: Long) {
        viewModelScope.launch {
            deleteCategoryUseCase.invoke(id)
        }
    }

    fun changeType(type: Transaction.Type) {
        viewModelScope.launch {
            _transactionType.emit(type)
        }
    }
}