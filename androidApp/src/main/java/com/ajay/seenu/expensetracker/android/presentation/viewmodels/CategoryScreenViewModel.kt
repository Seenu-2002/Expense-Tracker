package com.ajay.seenu.expensetracker.android.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.Category
import com.ajay.seenu.expensetracker.android.domain.usecases.category.AddCategoryUseCase
import com.ajay.seenu.expensetracker.android.domain.usecases.category.DeleteCategoryUseCase
import com.ajay.seenu.expensetracker.android.domain.usecases.category.GetAllCategoriesUseCase
import com.ajay.seenu.expensetracker.android.domain.usecases.category.GetCategoryUseCase
import com.ajay.seenu.expensetracker.android.domain.usecases.category.UpdateCategoryUseCase
import com.ajay.seenu.expensetracker.entity.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryScreenViewModel @Inject constructor(
) : ViewModel() {

    private val _categories: MutableStateFlow<List<Category>> = MutableStateFlow(emptyList())
    val categories = _categories.asStateFlow()

    private val _category: MutableStateFlow<Category?> = MutableStateFlow(null)
    val category = _category.asStateFlow()

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

    fun getAllCategories() {
        viewModelScope.launch {
            _categories.emit(emptyList())
            getCategoriesUseCase.invoke().collectLatest {
                _categories.emit(it)
            }
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
            addCategoryUseCase.invoke( label, transactionType, null )
        }
    }
}