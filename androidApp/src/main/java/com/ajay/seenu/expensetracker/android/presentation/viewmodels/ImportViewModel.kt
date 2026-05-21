package com.ajay.seenu.expensetracker.android.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.android.data.ImportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ImportState {
    data object Idle : ImportState()
    data object Loading : ImportState()
    data class Success(val importedCount: Int) : ImportState()
    data class Error(val message: String) : ImportState()
}

@HiltViewModel
class ImportViewModel @Inject constructor(
    private val repository: ImportRepository
) : ViewModel() {

    private val _state = MutableStateFlow<ImportState>(ImportState.Idle)
    val state: StateFlow<ImportState> = _state.asStateFlow()

    fun importFile(uri: Uri) {
        viewModelScope.launch {
            _state.value = ImportState.Loading
            val result = repository.importFromUri(uri)
            _state.value = if (result.success) {
                ImportState.Success(result.importedCount)
            } else {
                ImportState.Error(result.errorMessage ?: "Import failed")
            }
        }
    }

    fun reset() {
        _state.value = ImportState.Idle
    }
}
