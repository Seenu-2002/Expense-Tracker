package com.ajay.seenu.expensetracker

import com.ajay.seenu.expensetracker.entity.UserConfigs


expect class UserConfigurationsManager {

    fun isUserLoggedIn(): Boolean

    fun getConfigs(): UserConfigs

    fun storeConfigs(configs: UserConfigs)

}

object UserNotLoggedIn : Exception()