package com.ajay.seenu.expensetracker.android.presentation.viewmodels

import com.ajay.seenu.expensetracker.UserConfigurationsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    userConfigurationsManager: UserConfigurationsManager
) : ThemeAwareViewModel(userConfigurationsManager)