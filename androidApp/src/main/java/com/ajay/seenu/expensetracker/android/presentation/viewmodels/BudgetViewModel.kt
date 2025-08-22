package com.ajay.seenu.expensetracker.android.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.BudgetRepository
import com.ajay.seenu.expensetracker.Category
import com.ajay.seenu.expensetracker.android.data.CategoryRepository
import com.ajay.seenu.expensetracker.entity.TransactionType
import com.ajay.seenu.expensetracker.entity.budget.BudgetRequest
import com.ajay.seenu.expensetracker.entity.budget.BudgetWithSpending
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BudgetUiState())
    val uiState: StateFlow<BudgetUiState> = _uiState.asStateFlow()

    private val _budgets = budgetRepository.getAllBudgetsWithSpending()
    val budgets: StateFlow<List<BudgetWithSpending>> = _budgets.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val categories: StateFlow<List<Category>> = flow {
            emit(categoryRepository.getCategories(TransactionType.EXPENSE))
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedBudget = MutableStateFlow<BudgetWithSpending?>(null)
    val selectedBudget: StateFlow<BudgetWithSpending?> = _selectedBudget.asStateFlow()

    fun loadBudget(budgetId: Long) {
        viewModelScope.launch {
            try {
                val budget = budgetRepository.getBudgetWithSpending(budgetId)
                _selectedBudget.value = budget
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to load budget"
                )
            }
        }
    }

    fun createBudget(budgetRequest: BudgetRequest) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                budgetRepository.createBudget(budgetRequest)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    message = "Budget created successfully"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to create budget"
                )
            }
        }
    }

    fun updateBudget(budgetId: Long, budgetRequest: BudgetRequest) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                budgetRepository.updateBudget(budgetId, budgetRequest)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    message = "Budget updated successfully"
                )
                // Reload the budget
                loadBudget(budgetId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to update budget"
                )
            }
        }
    }

    fun deleteBudget(budgetId: Long) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                budgetRepository.deleteBudget(budgetId)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    message = "Budget deleted successfully"
                )
                _selectedBudget.value = null
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to delete budget"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
}

data class BudgetUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val message: String? = null
)