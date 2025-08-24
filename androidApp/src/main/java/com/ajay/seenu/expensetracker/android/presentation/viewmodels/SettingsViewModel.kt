package com.ajay.seenu.expensetracker.android.presentation.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.UserConfigurationsManager
import com.ajay.seenu.expensetracker.domain.DateFormats
import com.ajay.seenu.expensetracker.domain.model.Theme
import com.ajay.seenu.expensetracker.domain.model.UserConfigs
import com.ajay.seenu.expensetracker.domain.usecase.transaction.DeleteAllTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val configurationManager: UserConfigurationsManager,
) : ViewModel() {

    @Inject
    internal lateinit var deleteAllTransactionsUseCase: DeleteAllTransactionsUseCase

    private val defaultConfig = UserConfigs(
        "",
        null,
        Theme.SYSTEM_THEME,
        DayOfWeek.MONDAY,
        "dd MMM, yyyy",
        isAppLockEnabled = false
    )

    private val _userConfigs: MutableStateFlow<UserConfigs> = MutableStateFlow(defaultConfig)
    val userConfigs = _userConfigs.asStateFlow()

    internal val supportedDateFormats: List<Pair<String, String>> by lazy {
        val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH)
        val today = Date()
        DateFormats.FORMATS.map {
            simpleDateFormat.applyPattern(it)
            it to simpleDateFormat.format(today)
        }
    }

    fun getConfigs(context: Context) {
        viewModelScope.launch {
            val config = configurationManager.getConfigs()
            _userConfigs.emit(config)
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

    fun changeDateFormatPref(index: Int, format: String) {
        if (_userConfigs.value.dateFormat == format) {
            return
        }
        viewModelScope.launch {
            val newDateFormat = supportedDateFormats[index].first
            val newConfigs = _userConfigs.value.copy(dateFormat = newDateFormat)
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

    fun changeWeekStartFromPref(day: DayOfWeek) {
        if (_userConfigs.value.weekStartsFrom == day) {
            return
        }
        viewModelScope.launch {
            val newConfigs = _userConfigs.value.copy(weekStartsFrom = day)
            configurationManager.storeConfigs(newConfigs)
            _userConfigs.emit(newConfigs)
        }
    }

    fun deleteAllTransactions() {
        viewModelScope.launch {
            deleteAllTransactionsUseCase.invoke()
        }
    }

}