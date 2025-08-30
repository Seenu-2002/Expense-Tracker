package com.ajay.seenu.expensetracker.android.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.android.presentation.state.AccountsListUiModel
import com.ajay.seenu.expensetracker.android.presentation.state.Error
import com.ajay.seenu.expensetracker.android.presentation.state.UiState
import com.ajay.seenu.expensetracker.domain.usecase.account.GetAccountsAsFlowUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AccountsListViewModel @Inject constructor(
    private val getAccountsUseCase: GetAccountsAsFlowUseCase
) : ViewModel() {

    private val _accounts: MutableStateFlow<UiState<AccountsListUiModel>> =
        MutableStateFlow(UiState.Empty)
    val accounts: StateFlow<UiState<AccountsListUiModel>> = _accounts.asStateFlow()

    fun getAccounts() {
        viewModelScope.launch {
            _accounts.value = UiState.Loading
            getAccountsUseCase().collect {
                try {
                    _accounts.value = UiState.Success(AccountsListUiModel(it))
                } catch (e: Exception) {
                    Timber.e(e, "Error while fetching accounts")
                    _accounts.value = UiState.Failure(error = Error.Unhandled(e))
                }
            }
        }
    }

}