package com.ajay.seenu.expensetracker.android.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.android.presentation.state.Error
import com.ajay.seenu.expensetracker.android.presentation.state.UiState
import com.ajay.seenu.expensetracker.domain.model.Account
import com.ajay.seenu.expensetracker.domain.model.AccountType
import com.ajay.seenu.expensetracker.domain.usecase.account.CreateAccountUseCase
import com.ajay.seenu.expensetracker.domain.usecase.account.GetAccountUseCase
import com.ajay.seenu.expensetracker.domain.usecase.account.UpdateAccountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AddEditAccountViewModel @Inject constructor() : ViewModel() {

    @Inject
    internal lateinit var getAccountUseCase: GetAccountUseCase

    @Inject
    internal lateinit var createAccountUseCase: CreateAccountUseCase

    @Inject
    internal lateinit var updateAccountUseCase: UpdateAccountUseCase

    private val _account: MutableStateFlow<UiState<Account>> = MutableStateFlow(UiState.Empty)
    val account: StateFlow<UiState<Account>> = _account.asStateFlow()

    private val _status: MutableStateFlow<UiState<Boolean>> = MutableStateFlow(UiState.Empty)
    val status: StateFlow<UiState<Boolean>> = _status.asStateFlow()

    fun getAccount(id: Long) {
        viewModelScope.launch {
            _account.value = UiState.Loading
            try {
                val account = getAccountUseCase(id)
                if (account == null) {
                    _account.value = UiState.Failure(error = Error.AccountNotFound)
                    return@launch
                }

                _account.value = UiState.Success(account)
            } catch (e: Exception) {
                Timber.e(e, "Error fetching account")
                _account.value = UiState.Failure(error = Error.Unhandled(e))
            }
        }
    }

    fun createAccount(name: String, type: AccountType) {
        viewModelScope.launch {
            _status.value = UiState.Loading
            try {
                createAccountUseCase(name, type)
                _status.value = UiState.Success(true)
            } catch (e: Exception) {
                Timber.e(e, "Error creating account")
                _status.value = UiState.Failure(error = Error.Unhandled(e))
            }
        }
    }

    fun updateAccount(id: Long, name: String, type: AccountType) {
        viewModelScope.launch {
            _status.value = UiState.Loading
            try {
                updateAccountUseCase(id, name, type)
            } catch (e: Exception) {
                Timber.e(e, "Error updating account")
                _status.value = UiState.Failure(error = Error.Unhandled(e))
            }
        }
    }

}