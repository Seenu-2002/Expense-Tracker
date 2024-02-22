package com.ajay.seenu.expensetracker.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.Income
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

//@HiltViewModel
class IncomeViewModel(
    private val incomeRepository: IncomeRepository
): ViewModel() {
    val incomes = incomeRepository.getAllIncomes()

    fun addIncome(income: Income) {
        viewModelScope.launch {
            incomeRepository.addIncome(income)
        }
    }
}