package com.ajay.seenu.expensetracker.android.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.UserConfigurationsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(userConfigurationsManager: UserConfigurationsManager) : ThemeAwareViewModel(userConfigurationsManager) {

    private val _isUserLoggedIn: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isUserLoggedIn = _isUserLoggedIn.asStateFlow()

    init {
        Log.e("TAG", "INSTANCE")
    }

    override fun init() {
        super.init()
        viewModelScope.launch {
            _isUserLoggedIn.emit(userConfigurationsManager.isUserLoggedIn())
        }
    }

}