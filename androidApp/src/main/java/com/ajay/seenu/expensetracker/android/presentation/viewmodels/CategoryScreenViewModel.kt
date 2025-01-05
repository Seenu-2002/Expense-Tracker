package com.ajay.seenu.expensetracker.android.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.Category
import com.ajay.seenu.expensetracker.android.domain.data.Transaction
import com.ajay.seenu.expensetracker.android.domain.usecases.category.AddCategoryUseCase
import com.ajay.seenu.expensetracker.android.domain.usecases.category.DeleteCategoryUseCase
import com.ajay.seenu.expensetracker.android.domain.usecases.category.GetAllCategoriesUseCase
import com.ajay.seenu.expensetracker.android.domain.usecases.category.GetCategoryUseCase
import com.ajay.seenu.expensetracker.android.domain.usecases.category.UpdateCategoryUseCase
import com.ajay.seenu.expensetracker.entity.TransactionType
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

    private val _categories: MutableStateFlow<List<Transaction.Category>> = MutableStateFlow(emptyList())
    val categories = _categories.asStateFlow()

    private val _category: MutableStateFlow<Category?> = MutableStateFlow(null)
    val category = _category.asStateFlow()

    private val _transactionType: MutableStateFlow<Transaction.Type> = MutableStateFlow(Transaction.Type.INCOME)
    val transactionType: StateFlow<Transaction.Type> = _transactionType.asStateFlow()

    @Inject
    internal lateinit var getCategoriesUseCase: GetAllCategoriesUseCase

    @Inject
    internal lateinit var getCategoryUseCase: GetCategoryUseCase

    @Inject
    internal lateinit var deleteCategoryUseCase: DeleteCategoryUseCase

    @Inject
    internal lateinit var addCategoryUseCase: AddCategoryUseCase

    @Inject
    internal lateinit var updateCategoryUseCase: UpdateCategoryUseCase

    fun getCategories(type: Transaction.Type) {
        viewModelScope.launch {
            _categories.emit(emptyList()) // TODO: UIState
            _categories.emit(getCategoriesUseCase.invoke(type))
        }
    }

    fun getCategory(id: Long) {
        viewModelScope.launch {
            getCategoryUseCase.invoke(id).collectLatest {
                _category.emit(it)
            }
        }
    }

    fun deleteCategory(id: Long) {
        viewModelScope.launch {
            deleteCategoryUseCase.invoke(id)
        }
    }

    fun updateCategory(id: Long, label: String, type: TransactionType) {
        viewModelScope.launch {
            updateCategoryUseCase.invoke( id, label, type )
        }
    }

    fun addCategory( label: String, transactionType: TransactionType ) {
        viewModelScope.launch {
            TODO()
//            addCategoryUseCase.invoke( label, transactionType, null )
        }
    }

    fun changeType(type: Transaction.Type) {
        viewModelScope.launch {
            _transactionType.emit(type)
        }
    }
}