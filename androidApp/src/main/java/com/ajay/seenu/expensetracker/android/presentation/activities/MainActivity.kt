package com.ajay.seenu.expensetracker.android.presentation.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ajay.seenu.expensetracker.UserConfigurationsManager
import com.ajay.seenu.expensetracker.android.ExpenseTrackerTheme
import com.ajay.seenu.expensetracker.android.presentation.navigation.MainScreen
import com.ajay.seenu.expensetracker.android.presentation.navigation.Screen
import com.ajay.seenu.expensetracker.android.presentation.theme.AppDefaults
import com.ajay.seenu.expensetracker.android.presentation.theme.LocalColors
import com.ajay.seenu.expensetracker.android.presentation.screeens.AddTransactionScreen
import com.ajay.seenu.expensetracker.android.presentation.screeens.CategoryDetailScreen
import com.ajay.seenu.expensetracker.android.presentation.screeens.CategoryListScreen
import com.ajay.seenu.expensetracker.android.presentation.screeens.DetailTransactionScreen
import com.ajay.seenu.expensetracker.android.presentation.viewmodels.MainViewModel
import com.ajay.seenu.expensetracker.entity.Theme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    internal lateinit var userConfigurationManager: UserConfigurationsManager

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            viewModel.init()
        }

        setContent {
            val theme by viewModel.theme.collectAsStateWithLifecycle()
            val isDarkThemeEnabled = when (theme) {
                Theme.LIGHT -> false
                Theme.DARK -> true
                Theme.SYSTEM_THEME -> isSystemInDarkTheme()
            }
            ExpenseTrackerTheme(darkTheme = isDarkThemeEnabled) {
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
                                    onTransactionClicked = {
                                        navController.navigate("${Screen.DetailTransaction.route}/${it}")
                                    },
                                    onCloneTransaction = {
                                        navController.navigate("${Screen.AddTransaction.route}/${it}")
                                    },
                                    onCategoryListScreen = {
                                        navController.navigate(Screen.CategoryList.route)
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
                            composable("${Screen.DetailTransaction.route}/{transaction_id}",
                                arguments = listOf(
                                    navArgument("transaction_id") {
                                        type = NavType.LongType
                                    }
                                )
                            ) {
                                var cloneId = it.arguments?.getLong("transaction_id")
                                if (cloneId == -1L) cloneId = null
                                DetailTransactionScreen(
                                    onNavigateBack = {
                                        navController.popBackStack()
                                    },
                                    cloneId
                                )
                            }
                            composable(Screen.CategoryList.route) {
                                CategoryListScreen (
                                    categoryDetailScreen = {
                                        if( it == null ) {
                                            navController.navigate("${Screen.Category.route}/-1L")
                                        } else {
                                            navController.navigate( "${Screen.Category.route}/${it}")
                                        }
                                    },
                                    onNavigateBack = {
                                        navController.popBackStack()
                                    }
                                )
                            }
                            composable("${Screen.Category.route}/{clone_id}",
                                arguments = listOf(
                                    navArgument("clone_id") {
                                        type = NavType.LongType
                                    }
                                )
                            ) {
                                var cloneId = it.arguments?.getLong("clone_id")
                                if (cloneId == -1L) cloneId = null
                                CategoryDetailScreen(
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