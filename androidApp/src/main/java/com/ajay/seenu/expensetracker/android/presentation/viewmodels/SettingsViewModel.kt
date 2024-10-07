package com.ajay.seenu.expensetracker.android.presentation.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.UserConfigurationsManager
import com.ajay.seenu.expensetracker.entity.StartDayOfTheWeek
import com.ajay.seenu.expensetracker.entity.Theme
import com.ajay.seenu.expensetracker.entity.UserConfigs
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(@ApplicationContext private val context: Context) :
    ViewModel() {

    private val defaultConfig = UserConfigs(
        "",
        null,
        Theme.SYSTEM_THEME,
        StartDayOfTheWeek.MONDAY,
        "dd MMM, yyyy",
        isEncryptionEnabled = false,
        isAppLockEnabled = false
    )

    private val _userConfigs: MutableStateFlow<UserConfigs> = MutableStateFlow(defaultConfig)
    val userConfigs = _userConfigs.asStateFlow()

    private val configurationManager = UserConfigurationsManager(context)


    fun getConfigs(context: Context) {
        viewModelScope.launch {
            val config = configurationManager.getConfigs()
            _userConfigs.emit(config)
        }
    }

    fun shouldEnableEncryption(bool: Boolean) {
        if (_userConfigs.value.isEncryptionEnabled == bool) {
            return
        }
        viewModelScope.launch {
            val newConfigs = _userConfigs.value.copy(isEncryptionEnabled = bool)
            configurationManager.storeConfigs(newConfigs)
            _userConfigs.emit(newConfigs)
        }
    }

    fun shouldEnableAppLock(bool: Boolean) {
        if (_userConfigs.value.isAppLockEnabled == bool) {
            return
        }
        viewModelScope.launch {
            val newConfigs = _userConfigs.value.copy(isAppLockEnabled = bool)
            configurationManager.storeConfigs(newConfigs)
            _userConfigs.emit(newConfigs)
        }
    }

    fun changeDateFormatPref(format: String) {
        if (_userConfigs.value.dateFormat == format) {
            return
        }
        viewModelScope.launch {
            val newConfigs = _userConfigs.value.copy(dateFormat = format)
            configurationManager.storeConfigs(newConfigs)
            _userConfigs.emit(newConfigs)
        }
    }

    fun changeThemePref(theme: Theme) {
        if (_userConfigs.value.theme == theme) {
            return
        }
        viewModelScope.launch {
            val newConfigs = _userConfigs.value.copy(theme = theme)
            configurationManager.storeConfigs(newConfigs)
            _userConfigs.emit(newConfigs)
        }
    }

    fun changeWeekStartFromPref(day: StartDayOfTheWeek) {
        if (_userConfigs.value.weekStartsFrom == day) {
            return
        }
        viewModelScope.launch {
            val newConfigs = _userConfigs.value.copy(weekStartsFrom = day)
            configurationManager.storeConfigs(newConfigs)
            _userConfigs.emit(newConfigs)
        }
    }

}