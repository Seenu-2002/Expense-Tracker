package com.ajay.seenu.expensetracker.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ajay.seenu.expensetracker.android.presentation.navigation.MainScreen
import com.ajay.seenu.expensetracker.android.presentation.navigation.Screen
import com.ajay.seenu.expensetracker.android.presentation.screeens.AddTransactionScreen
import com.ajay.seenu.expensetracker.android.presentation.theme.AppDefaults
import com.ajay.seenu.expensetracker.android.presentation.theme.LocalColors
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            ExpenseTrackerTheme(darkTheme = false) {
                val appColors = AppDefaults.colors()
                CompositionLocalProvider(
                    LocalColors provides appColors
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val navController = rememberNavController()
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Default.route,
                            modifier = Modifier.fillMaxSize(),
                            route = "MainScreenRoute"
                        ) {
                            composable(Screen.Default.route) {
                                MainScreen(
                                    onAddTransaction = {
                                        navController.navigate("${Screen.AddTransaction.route}/-1L")
                                    },
                                    onCloneTransaction = {
                                        navController.navigate("${Screen.AddTransaction.route}/${it}")
                                    }
                                )
                            }
                            composable("${Screen.AddTransaction.route}/{clone_id}",
                                arguments = listOf(
                                    navArgument("clone_id") {
                                        type = NavType.LongType
                                    }
                                )
                            ) {
                                var cloneId = it.arguments?.getLong("clone_id")
                                if (cloneId == -1L) cloneId = null
                                AddTransactionScreen(
                                    onNavigateBack = {
                                        navController.popBackStack()
                                    },
                                    cloneId
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}