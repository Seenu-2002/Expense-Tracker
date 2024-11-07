package com.ajay.seenu.expensetracker

import com.ajay.seenu.expensetracker.entity.UserConfigs


@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class UserConfigurationsManager {

    suspend fun isUserLoggedIn(): Boolean

    suspend fun onUserLoggedIn()

    suspend fun onUserLoggedOut()

    suspend fun getConfigs(): UserConfigs

    suspend fun storeConfigs(configs: UserConfigs)

}

object UserNotLoggedIn : Exception()