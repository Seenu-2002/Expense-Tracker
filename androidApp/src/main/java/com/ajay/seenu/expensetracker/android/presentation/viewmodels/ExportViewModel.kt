package com.ajay.seenu.expensetracker.android.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.entity.ExportFormat
import com.ajay.seenu.expensetracker.entity.ExportState
import com.ajay.seenu.expensetracker.android.data.ExportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExportUiState(
    val exportState: ExportState = ExportState.Idle,
    val transactionCount: Int = 0,
    val selectedFormat: ExportFormat = ExportFormat.JSON,
    val showSuccessDialog: Boolean = false
)

@HiltViewModel
class ExportViewModel @Inject constructor(
    private val repository: ExportRepository
) : ViewModel() {

    private val _transactionCount = MutableStateFlow(0)
    private val _selectedFormat = MutableStateFlow(ExportFormat.JSON)
    private val _showSuccessDialog = MutableStateFlow(false)

    val uiState: StateFlow<ExportUiState> = combine(
        repository.exportState,
        _transactionCount,
        _selectedFormat,
        _showSuccessDialog
    ) { exportState, count, format, showDialog ->
        ExportUiState(
            exportState = exportState,
            transactionCount = count,
            selectedFormat = format,
            showSuccessDialog = showDialog
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ExportUiState()
    )

    init {
        loadTransactionCount()
    }

    fun selectFormat(format: ExportFormat) {
        _selectedFormat.value = format
    }

    fun exportAndSave() {
        viewModelScope.launch {
            val success = repository.exportAndSave(_selectedFormat.value)
            if (success) {
                _showSuccessDialog.value = true
            }
        }
    }

    fun exportAndShare() {
        viewModelScope.launch {
            repository.exportAndShare(_selectedFormat.value)
        }
    }

    fun dismissSuccessDialog() {
        _showSuccessDialog.value = false
        repository.resetExportState()
    }

    private fun loadTransactionCount() {
        viewModelScope.launch {
            _transactionCount.value = repository.getTransactionCount()
        }
    }
}