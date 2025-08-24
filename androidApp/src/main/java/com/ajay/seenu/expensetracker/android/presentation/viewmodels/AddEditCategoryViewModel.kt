package com.ajay.seenu.expensetracker.android.presentation.viewmodels

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.android.presentation.state.Error
import com.ajay.seenu.expensetracker.android.presentation.state.UiState
import com.ajay.seenu.expensetracker.domain.model.Category
import com.ajay.seenu.expensetracker.domain.model.TransactionType
import com.ajay.seenu.expensetracker.domain.usecase.category.AddCategoryUseCase
import com.ajay.seenu.expensetracker.domain.usecase.category.GetCategoryUseCase
import com.ajay.seenu.expensetracker.domain.usecase.category.UpdateCategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AddEditCategoryViewModel @Inject constructor(
    private val getCategoryUseCase: GetCategoryUseCase,
    private val addCategoryUseCase: AddCategoryUseCase,
    private val updateCategoryUseCase: UpdateCategoryUseCase
) : ViewModel() {

    private var _status: MutableStateFlow<UiState<String>> = MutableStateFlow(UiState.Empty)
    val status: StateFlow<UiState<String>?> = _status.asStateFlow()

    private val _category: MutableStateFlow<UiState<Category>> = MutableStateFlow(UiState.Empty)
    val category: StateFlow<UiState<Category>> = _category.asStateFlow()

    fun getCategory(id: Long) {
        viewModelScope.launch {
            _category.emit(UiState.Loading)
            try {
                val category = getCategoryUseCase(id)
                _category.emit(UiState.Success(category))
            } catch (exp: Exception) {
                Timber.e("Error fetching category: ${exp.message}")
                _category.emit(UiState.Failure(Error.CategoryNotFound))
            }
        }
    }

    fun createCategory(label: String, type: TransactionType, color: Color, res: Int) {
        viewModelScope.launch {
            try {
                addCategoryUseCase(label, type, res, color.toArgb().toLong())
                _status.emit(UiState.Success(label))
            } catch (exp: Exception) {
                Timber.e("Error adding category: ${exp.message}")
                _status.emit(UiState.Failure(Error.CategoryAlreadyPresent))
            }
        }
    }

    fun updateCategory(id: Long, label: String, type: TransactionType, color: Color, res: Int) {
        viewModelScope.launch {
            try {
                updateCategoryUseCase.invoke(
                    id = id,
                    label = label,
                    res = res,
                    color = color.toArgb().toLong()
                )
                _status.emit(UiState.Success(label))
            } catch (exp: Exception) {
                Timber.e("Error updating category: ${exp.message}")
                _status.emit(UiState.Failure(Error.CategoryAlreadyPresent))
            }
        }
    }

}