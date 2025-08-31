package com.ajay.seenu.expensetracker.android.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.android.data.FilterPreference
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
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
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedBudget = MutableStateFlow<BudgetWithSpending?>(null)
    val selectedBudget: StateFlow<BudgetWithSpending?> = _selectedBudget.asStateFlow()

    fun loadBudgets(filter: DateFilter) {
        viewModelScope.launch {
            _budgets.emit(UiState.Loading)
            val range = dateRangeCalculatorUseCase(filter)
            budgetRepository.getAllBudgetsWithSpending(range).collect {
                _budgets.emit(UiState.Success(it))
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
                //error
            }
        }
    }

    fun createBudget(budgetRequest: BudgetRequest) {
        viewModelScope.launch {
            try {
                budgetRepository.createBudget(budgetRequest)
            } catch (e: Exception) {
                //error
            }
        }
    }

    fun updateBudget(budgetId: Long, budgetRequest: BudgetRequest, filter: DateFilter) {
        viewModelScope.launch {
            try {
                budgetRepository.updateBudget(budgetId, budgetRequest)
                loadBudget(budgetId, filter)
            } catch (e: Exception) {
                //error
            }
        }
    }

    fun deleteBudget(budgetId: Long) {
        viewModelScope.launch {
            try {
                budgetRepository.deleteBudget(budgetId)
                _selectedBudget.value = null
            } catch (e: Exception) {
                //error
            }
        }
    }
}