package com.ajay.seenu.expensetracker

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ajay.seenu.expensetracker.domain.model.Theme
import com.ajay.seenu.expensetracker.domain.model.UserConfigs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import kotlinx.datetime.DayOfWeek

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class UserConfigurationsManager(private val appContext: Context) {

    private val theme = stringPreferencesKey("THEME")
    private val startDayOfWeek = stringPreferencesKey("START_DAY_OF_THE_WEEK")
    private val isAppLockEnabled = booleanPreferencesKey("IS_APP_LOCK_ENABLED")
    private val dateFormat = stringPreferencesKey("DATE_FORMAT")
    private val userName = stringPreferencesKey("USER_NAME")
    private val userImagePath = stringPreferencesKey("USER_IMAGE_PATH")
    private val isUserLoggedIn = booleanPreferencesKey("isUserLoggedIn")

    private val dataStore = createDataStore(appContext)

    actual suspend fun isUserLoggedIn(): Boolean {
        return dataStore.data.first()[isUserLoggedIn] ?: false
    }

    actual suspend fun onUserLoggedIn() {
        dataStore.edit {
            it[isUserLoggedIn] = true
        }
    }

    actual suspend fun onUserLoggedOut() {
        dataStore.edit {
            it.clear()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getTheme(): Flow<Theme> {
        return dataStore.data.mapLatest {
            it[theme]?.let { theme ->
                Theme.valueOf(theme)
            } ?: Theme.SYSTEM_THEME
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getDateFormat(): Flow<String> {
        return dataStore.data.mapLatest {
            it[dateFormat] ?: "dd MMM, yyyy"
        }
    }

    @Throws(UserNotLoggedIn::class)
    actual suspend fun getConfigs(): UserConfigs {
        if (!isUserLoggedIn()) {
            throw UserNotLoggedIn
        }

        return dataStore.data.first().let {
            UserConfigs(
                name = it[userName] ?: "",
                userImagePath = it[userImagePath],
                theme = it[theme]?.let { theme ->
                    Theme.valueOf(theme)
                } ?: Theme.SYSTEM_THEME,
                weekStartsFrom = it[startDayOfWeek]?.let { startDayOfWeek ->
                    DayOfWeek.valueOf(startDayOfWeek)
                } ?: DayOfWeek.SUNDAY,
                dateFormat = it[dateFormat] ?: "dd MMM, yyyy",
                isAppLockEnabled = it[isAppLockEnabled] ?: false
            )
        }
    }

    actual suspend fun storeConfigs(configs: UserConfigs) {
        dataStore.edit {
            it[userName] = configs.name
            configs.userImagePath?.let { imagePath ->
                it[userImagePath] = imagePath
            }
            it[theme] = configs.theme.toString()
            it[startDayOfWeek] = configs.weekStartsFrom.toString()
            it[dateFormat] = configs.dateFormat
            it[isAppLockEnabled] = configs.isAppLockEnabled
        }
    }

}

fun createDataStore(context: Context): DataStore<Preferences> = createDataStore(
    producePath = { context.filesDir.resolve(dataStoreFileName).absolutePath }
)