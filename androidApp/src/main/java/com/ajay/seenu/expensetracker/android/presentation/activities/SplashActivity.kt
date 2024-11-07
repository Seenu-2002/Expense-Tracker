package com.ajay.seenu.expensetracker.android.presentation.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.ajay.seenu.expensetracker.android.presentation.viewmodels.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : ComponentActivity() {

    private lateinit var splashScreen: SplashScreen
    private val viewModel: SplashViewModel by viewModels<SplashViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { true }
        viewModel.init()
        attachObserver()
    }

    private fun attachObserver() {
        lifecycleScope.launch {
            viewModel.isUserLoggedIn.collectLatest {
                val targetActivityKClass: KClass<*> = when (it) {
                    true -> {
                        MainActivity::class
                    }

                    false -> {
                        OnboardingActivity::class
                    }
                }
                if (!isDestroyed) {
                    startActivity(
                        Intent(
                            this@SplashActivity,
                            targetActivityKClass.java
                        ).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        })
                    finish()
                }
            }
        }
    }
}