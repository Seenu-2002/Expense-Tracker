package com.ajay.seenu.expensetracker

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.ajay.seenu.expensetracker.domain.model.UserConfigs
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

actual class UserConfigurationsManager {
    actual suspend fun isUserLoggedIn(): Boolean {
        TODO("Not yet implemented")
    }

    actual suspend fun onUserLoggedIn() {
        TODO("Not yet implemented")
    }

    actual suspend fun onUserLoggedOut() {
        TODO("Not yet implemented")
    }

    actual suspend fun getConfigs(): UserConfigs {
        TODO("Not yet implemented")
    }

    actual suspend fun storeConfigs(configs: UserConfigs) {
    }

    actual fun getCurrencySymbolAsFlow(): Flow<String> = flowOf("$")

}

@OptIn(ExperimentalForeignApi::class)
fun createDataStore(): DataStore<Preferences> = createDataStore(
    producePath = {
        val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        requireNotNull(documentDirectory).path + "/$dataStoreFileName"
    }
)