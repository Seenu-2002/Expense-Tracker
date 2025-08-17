package com.ajay.seenu.expensetracker.android

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import com.ajay.seenu.expensetracker.android.BuildConfig

@HiltAndroidApp
class ExpenseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

}