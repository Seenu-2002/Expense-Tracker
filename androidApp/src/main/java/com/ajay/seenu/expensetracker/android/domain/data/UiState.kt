package com.ajay.seenu.expensetracker.android.domain.data

sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Failure(val error: Error) : UiState<Nothing>()
}

sealed interface Error {
    data object Empty : Error
    data class Unhandled(val exception: Throwable) : Error
}