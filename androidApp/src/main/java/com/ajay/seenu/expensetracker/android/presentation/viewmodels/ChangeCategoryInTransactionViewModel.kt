package com.ajay.seenu.expensetracker.android.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.android.domain.data.Error
import com.ajay.seenu.expensetracker.android.domain.data.Transaction
import com.ajay.seenu.expensetracker.android.domain.data.UiState
import com.ajay.seenu.expensetracker.android.domain.usecases.ChangeCategoriesUseCase
import com.ajay.seenu.expensetracker.android.domain.usecases.category.DeleteCategoryUseCase
import com.ajay.seenu.expensetracker.android.domain.usecases.category.GetAllCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ChangeCategoryInTransactionViewModel @Inject constructor() : ViewModel() {

    @Inject
    internal lateinit var getCategoriesUseCase: GetAllCategoriesUseCase

    @Inject
    internal lateinit var changeCategoriesUseCase: ChangeCategoriesUseCase

    @Inject
    internal lateinit var deleteCategoryUseCase: DeleteCategoryUseCase


    private var _categoryIdToBeDeleted: Transaction.Category? = null
    val categoryToBeDeleted: Transaction.Category
        get() = _categoryIdToBeDeleted!!

    private val _categories: MutableStateFlow<UiState<List<Transaction.Category>>> =
        MutableStateFlow(UiState.Empty)
    val categories: StateFlow<UiState<List<Transaction.Category>>> = _categories.asStateFlow()

    private val _updateStatus: MutableStateFlow<UiState<Boolean>> = MutableStateFlow(UiState.Empty)
    val updateStatus: StateFlow<UiState<Boolean>> = _updateStatus.asStateFlow()

    private val _transactionCount: MutableStateFlow<UiState<Long>> = MutableStateFlow(UiState.Empty)
    val transactionCount: StateFlow<UiState<Long>> = _transactionCount.asStateFlow()

    fun init(type: Transaction.Type, categoryToBeDeletedId: Long) {
        viewModelScope.launch {
            try {
                _categories.value = UiState.Loading
                val categories = getCategoriesUseCase(type)
                Timber.d("Fetched categories: $categories")
                val categoryToBeDeleted = categories.find { it.id == categoryToBeDeletedId }
                if (categoryToBeDeleted == null) {
                    _categories.value = UiState.Failure(Error.CategoryNotFound)
                    return@launch
                }

                _categoryIdToBeDeleted = categoryToBeDeleted
                _categories.value =
                    UiState.Success(categories.filter { it.id != categoryToBeDeletedId })
            } catch (exp: Exception) {
                Timber.e("Error fetching categories: ${exp.message}")
                _categories.value = UiState.Failure(Error.Unhandled(exp))
            }
        }
    }

    fun updateCategory(category: Transaction.Category) {
        if (category == categoryToBeDeleted) {
            throw IllegalArgumentException("Cannot update the category that is being deleted")
        }

        viewModelScope.launch {
            _updateStatus.value = UiState.Loading
            try {
                changeCategoriesUseCase(categoryToBeDeleted, category)
                deleteCategoryUseCase(categoryToBeDeleted.id)
                _updateStatus.value = UiState.Success(true)
            } catch (exp: Exception) {
                Timber.e("Error updating category: ${exp.message}")
                _updateStatus.value = UiState.Failure(Error.Unhandled(exp))
            }
        }
    }
}