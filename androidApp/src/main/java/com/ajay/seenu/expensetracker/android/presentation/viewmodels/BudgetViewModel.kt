package com.ajay.seenu.expensetracker.android.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.android.data.FilterPreference
import com.ajay.seenu.expensetracker.android.presentation.state.Error
import com.ajay.seenu.expensetracker.android.presentation.state.UiState
import com.ajay.seenu.expensetracker.data.repository.BudgetRepository
import com.ajay.seenu.expensetracker.data.repository.CategoryRepository
import com.ajay.seenu.expensetracker.domain.model.Category
import com.ajay.seenu.expensetracker.domain.model.DateFilter
import com.ajay.seenu.expensetracker.domain.model.TransactionType
import com.ajay.seenu.expensetracker.domain.model.budget.BudgetRequest
import com.ajay.seenu.expensetracker.domain.model.budget.BudgetWithSpending
import com.ajay.seenu.expensetracker.domain.usecase.DateRangeCalculatorUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
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

    @Inject
    internal lateinit var dateRangeCalculatorUseCase: DateRangeCalculatorUseCase

//    private val _uiState = MutableStateFlow(BudgetUiState())
//    val uiState: StateFlow<BudgetUiState> = _uiState.asStateFlow()

    private val _budgets: MutableStateFlow<UiState<List<BudgetWithSpending>>> = MutableStateFlow(UiState.Loading)
    val budgets: StateFlow<UiState<List<BudgetWithSpending>>> = _budgets.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UiState.Loading
    )

    val categories: StateFlow<List<Category>> = flow {
            emit(categoryRepository.getCategories(TransactionType.EXPENSE))
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    private val _selectedBudget = MutableStateFlow<BudgetWithSpending?>(null)
    val selectedBudget: StateFlow<BudgetWithSpending?> = _selectedBudget.asStateFlow()

    private val _events = MutableSharedFlow<BudgetEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<BudgetEvent> = _events.asSharedFlow()

    fun loadBudgets(filter: DateFilter) {
        viewModelScope.launch {
            try {
                _budgets.emit(UiState.Loading)
                val range = dateRangeCalculatorUseCase(filter)
                budgetRepository.getAllBudgetsWithSpending(range).collect {
                    _budgets.emit(UiState.Success(it))
                }
            } catch (e: Exception) {
                _budgets.emit(UiState.Failure(Error.Unhandled(e)))
            }
        }
    }

    fun loadBudget(budgetId: Long, filter: DateFilter) {
        viewModelScope.launch {
            try {
                val range = dateRangeCalculatorUseCase(filter)
                val budget = budgetRepository.getBudgetWithSpending(budgetId, range)
                _selectedBudget.value = budget
            } catch (e: Exception) {
                _events.emit(BudgetEvent.Error(e.message ?: "Failed to load budget"))
            }
        }
    }

    fun createBudget(budgetRequest: BudgetRequest) {
        viewModelScope.launch {
            try {
                budgetRepository.createBudget(budgetRequest)
                _events.emit(BudgetEvent.OperationSuccess)
            } catch (e: Exception) {
                _events.emit(BudgetEvent.Error(e.message ?: "Failed to create budget"))
            }
        }
    }

    fun updateBudget(budgetId: Long, budgetRequest: BudgetRequest, filter: DateFilter) {
        viewModelScope.launch {
            try {
                budgetRepository.updateBudget(budgetId, budgetRequest)
                loadBudget(budgetId, filter)
                _events.emit(BudgetEvent.OperationSuccess)
            } catch (e: Exception) {
                _events.emit(BudgetEvent.Error(e.message ?: "Failed to update budget"))
            }
        }
    }

    fun deleteBudget(budgetId: Long) {
        viewModelScope.launch {
            try {
                budgetRepository.deleteBudget(budgetId)
                _selectedBudget.value = null
                _events.emit(BudgetEvent.OperationSuccess)
            } catch (e: Exception) {
                _events.emit(BudgetEvent.Error(e.message ?: "Failed to delete budget"))
            }
        }
    }

    sealed class BudgetEvent {
        data object OperationSuccess : BudgetEvent()
        data class Error(val message: String) : BudgetEvent()
    }
}