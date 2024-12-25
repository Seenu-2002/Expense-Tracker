package com.ajay.seenu.expensetracker.android.domain.data

sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data object Failure : UiState<Nothing>()
}