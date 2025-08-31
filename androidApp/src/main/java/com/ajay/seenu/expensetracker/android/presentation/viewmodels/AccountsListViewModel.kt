package com.ajay.seenu.expensetracker.android.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.android.presentation.state.AccountsListUiModel
import com.ajay.seenu.expensetracker.android.presentation.state.Error
import com.ajay.seenu.expensetracker.android.presentation.state.UiState
import com.ajay.seenu.expensetracker.domain.model.Account
import com.ajay.seenu.expensetracker.domain.usecase.account.DeleteAccountUseCase
import com.ajay.seenu.expensetracker.domain.usecase.account.GetAccountsAsFlowUseCase
import com.ajay.seenu.expensetracker.domain.usecase.transaction.GetTransactionCountByAccountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AccountsListViewModel @Inject constructor(
    private val getAccountsUseCase: GetAccountsAsFlowUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val getTransactionCountByAccountUseCase: GetTransactionCountByAccountUseCase
) : ViewModel() {

    private val _accounts: MutableStateFlow<UiState<AccountsListUiModel>> =
        MutableStateFlow(UiState.Empty)
    val accounts: StateFlow<UiState<AccountsListUiModel>> = _accounts.asStateFlow()

    private val _transactionCountInAccount: MutableStateFlow<UiState<Long>> = MutableStateFlow(UiState.Empty)
    val transactionCountInAccount: StateFlow<UiState<Long>> = _transactionCountInAccount.asStateFlow()

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

    fun getTransactionCountInAccount(account: Account) {
        viewModelScope.launch {
            _transactionCountInAccount.value = UiState.Loading
            try {
                val count = getTransactionCountByAccountUseCase(account)
                _transactionCountInAccount.value = UiState.Success(count)
            } catch (e: Exception) {
                Timber.e(e, "Error while fetching transaction count for accountId: ${account.id}")
                _transactionCountInAccount.value = UiState.Failure(Error.Unhandled(e))
            }
        }
    }

    fun deleteAccount(account: Account) {
        viewModelScope.launch {
            try {
                deleteAccountUseCase(account)
            } catch (e: Exception) {
                Timber.e(e, "Error while deleting account")
            }
        }
    }

    fun resetCount() {
        viewModelScope.launch {
            _transactionCountInAccount.value = UiState.Empty
        }
    }

}