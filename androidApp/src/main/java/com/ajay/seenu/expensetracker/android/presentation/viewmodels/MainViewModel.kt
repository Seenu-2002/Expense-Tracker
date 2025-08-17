package com.ajay.seenu.expensetracker.android.presentation.viewmodels

import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.UserConfigurationsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    userConfigurationsManager: UserConfigurationsManager
) : ThemeAwareViewModel(userConfigurationsManager) {
    private val _isAppLockEnabled: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isAppLockEnabled = _isAppLockEnabled.asStateFlow()

    fun getIsAppLockEnabled() {
        viewModelScope.launch {
            _isAppLockEnabled.emit(userConfigurationsManager.getConfigs().isAppLockEnabled)
        }
    }
}