package com.ajay.seenu.expensetracker.android.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.android.presentation.state.Error
import com.ajay.seenu.expensetracker.android.presentation.state.UiState
import com.ajay.seenu.expensetracker.domain.model.Category
import com.ajay.seenu.expensetracker.domain.model.TransactionType
import com.ajay.seenu.expensetracker.domain.usecase.category.DeleteCategoryUseCase
import com.ajay.seenu.expensetracker.domain.usecase.category.GetAllCategoriesAsFlowUseCase
import com.ajay.seenu.expensetracker.domain.usecase.transaction.GetTransactionCountByCategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CategoryScreenViewModel @Inject constructor(
) : ViewModel() {
    
    private val _categoriesUiData: MutableStateFlow<UiState<CategoriesListUiData>> = MutableStateFlow(UiState.Empty)
    val categoriesUiData: StateFlow<UiState<CategoriesListUiData>> = _categoriesUiData.asStateFlow()

    private val _category: MutableStateFlow<Category?> = MutableStateFlow(null)
    val category = _category.asStateFlow()

    private val _transactionType: MutableStateFlow<TransactionType> =
        MutableStateFlow(TransactionType.INCOME)
    val transactionType: StateFlow<TransactionType> = _transactionType.asStateFlow()

    private val _transactionCount: MutableStateFlow<UiState<Pair<Long, Category>>> = MutableStateFlow(UiState.Empty)
    val transactionCount = _transactionCount.asStateFlow()

    @Inject
    internal lateinit var getCategoriesUseCase: GetAllCategoriesAsFlowUseCase

    @Inject
    internal lateinit var deleteCategoryUseCase: DeleteCategoryUseCase

    @Inject
    internal lateinit var getTransactionCountByCategoryUseCase: GetTransactionCountByCategoryUseCase
    
    fun getCategories() {
        viewModelScope.launch { 
            _categoriesUiData.emit(UiState.Loading)
            try {
                val incomeCategoriesFlow = getCategoriesUseCase(TransactionType.INCOME)
                val expenseCategoriesFlow = getCategoriesUseCase(TransactionType.EXPENSE)
                incomeCategoriesFlow.combine(expenseCategoriesFlow) { incomeCategories, expenseCategories ->
                    CategoriesListUiData(
                        incomeCategories = incomeCategories,
                        expenseCategories = expenseCategories
                    )
                }.collectLatest {
                    _categoriesUiData.emit(UiState.Success(it))
                }
            } catch (e: Exception) {
                Timber.e(e, "Error fetching categories")
                _categoriesUiData.emit(UiState.Failure(Error.Unhandled(e)))
            }
        }
    }

    fun getTransactionCountByCategory(category: Category) {
        viewModelScope.launch {
            _transactionCount.emit(UiState.Loading)
            try {
                _transactionType.emit(_category.value?.type ?: TransactionType.INCOME)
                _transactionCount.emit(UiState.Loading)
                val count = getTransactionCountByCategoryUseCase(category)
                _transactionCount.emit(UiState.Success(count to category))
            } catch (e: Exception) {
                Timber.e(e, "Error fetching transaction count")
                _transactionCount.emit(UiState.Failure(Error.Unhandled(e)))
            }
        }
    }

    fun deleteCategory(id: Long) {
        viewModelScope.launch {
            deleteCategoryUseCase.invoke(id)
        }
    }

    fun changeType(type: TransactionType) {
        viewModelScope.launch {
            _transactionType.emit(type)
        }
    }
}

data class CategoriesListUiData constructor(
    val incomeCategories: List<Category>,
    val expenseCategories: List<Category>
)