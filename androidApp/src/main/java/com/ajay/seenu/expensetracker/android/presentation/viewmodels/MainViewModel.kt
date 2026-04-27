package com.ajay.seenu.expensetracker.android.presentation.viewmodels

import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.UserConfigurationsManager
import com.ajay.seenu.expensetracker.domain.usecase.transaction.PurgeOldTrashUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    userConfigurationsManager: UserConfigurationsManager
) : ThemeAwareViewModel(userConfigurationsManager) {
    private val _isAppLockEnabled: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isAppLockEnabled = _isAppLockEnabled.asStateFlow()

    private val _currencySymbol: MutableStateFlow<String> = MutableStateFlow("$")
    val currencySymbol = _currencySymbol.asStateFlow()

    @Inject
    internal lateinit var purgeOldTrashUseCase: PurgeOldTrashUseCase

    override fun init() {
        super.init()
        viewModelScope.launch {
            try {
                purgeOldTrashUseCase()
            } catch (e: Exception) {
                Timber.e(e, "Error purging old trash")
            }
        }
    }

    fun getIsAppLockEnabled() {
        viewModelScope.launch {
            val configs = userConfigurationsManager.getConfigs()
            _isAppLockEnabled.emit(configs.isAppLockEnabled)
            _currencySymbol.emit(configs.currencySymbol)
        }
    }
}