package com.ajay.seenu.expensetracker.android.presentation.viewmodels

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.UserConfigurationsManager
import com.ajay.seenu.expensetracker.entity.Theme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

abstract class ThemeAwareViewModel(
    protected val userConfigurationsManager: UserConfigurationsManager
) : ViewModel() {

    private val _theme: MutableStateFlow<Theme> = MutableStateFlow(Theme.SYSTEM_THEME)
    val theme = _theme.asStateFlow()

    @CallSuper
    open fun init() {
        viewModelScope.launch {
            userConfigurationsManager.getTheme().collectLatest {
                _theme.emit(it)
            }
        }
    }

}