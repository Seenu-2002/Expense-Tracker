package com.ajay.seenu.expensetracker

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.ajay.seenu.expensetracker.entity.StartDayOfTheWeek
import com.ajay.seenu.expensetracker.entity.Theme
import com.ajay.seenu.expensetracker.entity.UserConfigs
import kotlin.jvm.Throws

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class UserConfigurationsManager(private val appContext: Context) {

    companion object {
        private const val PREF_NAME = "USER_CONFIGURATIONS"
        private const val IS_DATA_ENCRYPTION_ENABLED = "IS_DATA_ENCRYPTION_ENABLED"
        private const val THEME = "THEME"
        private const val START_DAY_OF_THE_WEEK = "START_DAY_OF_THE_WEEK"
        private const val IS_APP_LOCK_ENABLED = "IS_APP_LOCK_ENABLED"
        private const val DATE_FORMAT = "DATE_FORMAT"
        private const val USER_NAME = "USER_NAME"
        private const val USER_IMAGE_PATH = "USER_IMAGE_PATH"
        private const val IS_USER_LOGGED_IN = ""
    }

    actual fun isUserLoggedIn(): Boolean {
        return getSharedPreference().getBoolean(IS_USER_LOGGED_IN, false)
    }

    @Throws(UserNotLoggedIn::class)
    actual fun getConfigs(): UserConfigs {
        if (!isUserLoggedIn()) {
            throw UserNotLoggedIn
        }

        val pref = getSharedPreference()
        return UserConfigs(
            name = pref.getString(USER_NAME, "") ?: "",
            userImagePath = pref.getString(USER_IMAGE_PATH, null),
            theme = Theme.valueOf(
                pref.getString(THEME, Theme.SYSTEM_THEME.name) ?: Theme.SYSTEM_THEME.name
            ),
            weekStartsFrom = StartDayOfTheWeek.valueOf(
                pref.getString(
                    START_DAY_OF_THE_WEEK,
                    StartDayOfTheWeek.MONDAY.name
                ) ?: StartDayOfTheWeek.MONDAY.name
            ),
            dateFormat = pref.getString(DATE_FORMAT, "dd MMM, yyyy") ?: "dd MMM, yyyy",
            isEncryptionEnabled = pref.getBoolean(IS_DATA_ENCRYPTION_ENABLED, false),
            isAppLockEnabled = pref.getBoolean(IS_APP_LOCK_ENABLED, false)
        )
    }

    private fun getSharedPreference(): SharedPreferences {
        val masterKey = MasterKey.Builder(appContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val encryptedPreferences = EncryptedSharedPreferences.create(
            appContext,
            appContext.packageName + "_$PREF_NAME",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        return encryptedPreferences
    }

    actual fun storeConfigs(configs: UserConfigs) {
        getSharedPreference().edit()
            .putBoolean(IS_USER_LOGGED_IN, true)
            .putString(USER_NAME, configs.name)
            .putString(USER_IMAGE_PATH, configs.userImagePath)
            .putString(THEME, configs.theme.name)
            .putString(START_DAY_OF_THE_WEEK, configs.weekStartsFrom.name)
            .putString(DATE_FORMAT, configs.dateFormat)
            .putBoolean(IS_APP_LOCK_ENABLED, configs.isAppLockEnabled)
            .putBoolean(IS_DATA_ENCRYPTION_ENABLED, configs.isEncryptionEnabled)
            .apply()
    }

}