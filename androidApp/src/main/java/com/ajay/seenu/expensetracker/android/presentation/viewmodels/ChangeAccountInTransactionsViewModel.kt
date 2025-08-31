package com.ajay.seenu.expensetracker.android.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.android.presentation.state.Error
import com.ajay.seenu.expensetracker.android.presentation.state.UiState
import com.ajay.seenu.expensetracker.domain.model.Account
import com.ajay.seenu.expensetracker.domain.usecase.account.DeleteAccountUseCase
import com.ajay.seenu.expensetracker.domain.usecase.account.GetAccountsUseCase
import com.ajay.seenu.expensetracker.domain.usecase.transaction.ChangeAccountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ChangeAccountInTransactionsViewModel @Inject constructor(
    private val getAccountsUseCase: GetAccountsUseCase,
    private val changeAccountUseCase: ChangeAccountUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase
) : ViewModel() {

    private var _accountIdToBeDeleted: Account? = null
    val accountToBeDeleted: Account
        get() = _accountIdToBeDeleted!!

    private val _accounts: MutableStateFlow<UiState<List<Account>>> =
        MutableStateFlow(UiState.Empty)
    val accounts: StateFlow<UiState<List<Account>>> = _accounts.asStateFlow()

    private val _updateStatus: MutableStateFlow<UiState<Boolean>> = MutableStateFlow(UiState.Empty)
    val updateStatus: StateFlow<UiState<Boolean>> = _updateStatus.asStateFlow()

    fun init(accountToBeDeletedId: Long) {
        viewModelScope.launch {
            try {
                _accounts.value = UiState.Loading
                val accounts = getAccountsUseCase()

                val accountToBeDeleted = accounts.find { it.id == accountToBeDeletedId }
                this@ChangeAccountInTransactionsViewModel._accountIdToBeDeleted = accountToBeDeleted
                if (accountToBeDeleted == null) {
                    _accounts.value = UiState.Failure(Error.AccountNotFound)
                    return@launch
                }


                _accounts.value = UiState.Success(accounts.filter { it.id != accountToBeDeletedId })
            } catch (e: Exception) {
                Timber.e(e, "Error while fetching accounts")
                _accounts.value = UiState.Failure(
                    Error.Unhandled(e)
                )
            }
        }
    }

    fun updateAccount(account: Account) {
        if (account == accountToBeDeleted) {
            throw IllegalArgumentException("Cannot change to the same account")
        }

        viewModelScope.launch {
            _updateStatus.value = UiState.Loading
            try {
                changeAccountUseCase(accountToBeDeleted, account)
                deleteAccountUseCase(accountToBeDeleted)
                _updateStatus.value = UiState.Success(true)
            } catch (e: Exception) {
                Timber.e(e, "Error while changing account in transactions")
                _updateStatus.value = UiState.Failure(Error.Unhandled(e))
            }
        }
    }

}