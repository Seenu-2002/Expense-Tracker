package com.ajay.seenu.expensetracker.android.presentation.state

sealed class UiState<out T> {
    data object Empty : UiState<Nothing>()
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Failure(val error: Error) : UiState<Nothing>()
}

sealed interface Error {
    data object Empty : Error
    data object CategoryAlreadyPresent : Error
    data class Unhandled(val exception: Throwable) : Error
    data object CategoryNotFound : Error
}