package com.ajay.seenu.expensetracker.android.presentation.viewmodels

import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.UserConfigurationsManager
import com.ajay.seenu.expensetracker.android.domain.usecases.InsertDefaultCategories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    userConfigurationManager: UserConfigurationsManager,
    private val insertDefaultCategories: InsertDefaultCategories
) : ThemeAwareViewModel(userConfigurationManager) {

    private val _onUserLoggedIn: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val onUserLoggedIn = _onUserLoggedIn.asStateFlow()

    fun completeOnboarding() {
        viewModelScope.launch {
            userConfigurationsManager.onUserLoggedIn()
            insertDefaultCategories.invoke()
            _onUserLoggedIn.emit(true)
        }
    }

}