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
import com.ajay.seenu.expensetracker.android.data.TransactionMode
import com.ajay.seenu.expensetracker.android.domain.data.Transaction
import com.ajay.seenu.expensetracker.android.presentation.navigation.MainScreen
import com.ajay.seenu.expensetracker.android.presentation.navigation.Screen
import com.ajay.seenu.expensetracker.android.presentation.screeens.AddEditCategoryScreen
import com.ajay.seenu.expensetracker.android.presentation.screeens.AddEditScreenArg
import com.ajay.seenu.expensetracker.android.presentation.screeens.CategoryListScreen
import com.ajay.seenu.expensetracker.android.presentation.screeens.ChangeCategoryInTransactionScreen
import com.ajay.seenu.expensetracker.android.presentation.screeens.DetailTransactionScreen
import com.ajay.seenu.expensetracker.android.presentation.screeens.TransactionScreen
import com.ajay.seenu.expensetracker.android.presentation.theme.AppDefaults
import com.ajay.seenu.expensetracker.android.presentation.theme.LocalColors
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
                                        navController.navigate("${Screen.AddTransaction.route}/-1L/new")
                                    },
                                    onTransactionClicked = {
                                        navController.navigate("${Screen.DetailTransaction.route}/${it}")
                                    },
                                    onCloneTransaction = {
                                        navController.navigate("${Screen.AddTransaction.route}/${it}/clone")
                                    },
                                    onCategoryListScreen = {
                                        navController.navigate(Screen.CategoryList.route)
                                    }
                                )
                            }
                            composable(
                                "${Screen.AddTransaction.route}/{transaction_id}/{mode}",
                                arguments = listOf(
                                    navArgument("transaction_id") {
                                        type = NavType.LongType
                                    },
                                    navArgument("mode") {
                                        type = NavType.StringType
                                    }
                                )
                            ) {
                                val transactionId = it.arguments?.getLong("transaction_id")
                                val mode = it.arguments?.getString("mode")
                                val transactionMode =
                                    if (transactionId == -1L || transactionId == null) {
                                        TransactionMode.New
                                    } else {
                                        mode?.let {
                                            when (mode) {
                                                "clone" -> TransactionMode.Clone(transactionId)
                                                "edit" -> TransactionMode.Edit(transactionId)
                                                "new" -> TransactionMode.New
                                                else -> TransactionMode.New
                                            }
                                        } ?: TransactionMode.New
                                    }
                                TransactionScreen(
                                    transactionMode,
                                    onNavigateBack = {
                                        navController.popBackStack()
                                    }
                                )
                            }
                            composable(
                                "${Screen.DetailTransaction.route}/{transaction_id}",
                                arguments = listOf(
                                    navArgument("transaction_id") {
                                        type = NavType.LongType
                                    }
                                )
                            ) {
                                var cloneId = it.arguments?.getLong("transaction_id")
                                if (cloneId == -1L) cloneId = null
                                DetailTransactionScreen(
                                    cloneId,
                                    onEditTransaction = {
                                        navController.navigate("${Screen.AddTransaction.route}/$cloneId/edit")
                                    },
                                    onNavigateBack = {
                                        navController.popBackStack()
                                    }
                                )
                            }
                            composable(Screen.CategoryList.route) {
                                CategoryListScreen(
                                    onCreateCategory = { type ->
                                        navController.navigate("${Screen.Category.route}/-1/${type.name.uppercase()}")
                                    },
                                    onCategoryEdit = { id ->
                                        navController.navigate("${Screen.Category.route}/$id/${Transaction.Type.EXPENSE.name.uppercase()}")
                                    },
                                    onDeleteCategory = { id, type, count ->
                                        navController.navigate("${Screen.ChangeCategoryInTransaction.route}/$id/${type.name}/${count}")
                                    },
                                    onNavigateBack = {
                                        navController.popBackStack()
                                    }
                                )
                            }
                            composable(
                                "${Screen.Category.route}/{id}/{type}",
                                arguments = listOf(
                                    navArgument("id") {
                                        type = NavType.LongType
                                    },
                                    navArgument("type") {
                                        type = NavType.StringType
                                    }
                                )
                            ) {
                                var id = it.arguments?.getLong("id", -1L)
                                if (id == -1L) {
                                    id = null
                                }
                                val type = it.arguments?.getString("type")
                                    ?.let { Transaction.Type.valueOf(it.uppercase()) }

                                val arg = if (id != null) {
                                    AddEditScreenArg.Edit(id)
                                } else {
                                    AddEditScreenArg.Create(type ?: Transaction.Type.EXPENSE)
                                }
                                AddEditCategoryScreen(
                                    arg = arg,
                                    onNavigateBack = {
                                        navController.popBackStack()
                                    }
                                )
                            }
                            composable(
                                route = "${Screen.ChangeCategoryInTransaction.route}/{id}/{type}/{count}",
                                arguments = listOf(
                                    navArgument("id") {
                                        type = NavType.LongType
                                    },
                                    navArgument("type") {
                                        type = NavType.StringType
                                    },
                                    navArgument("count") {
                                        type = NavType.LongType
                                    }
                                )
                            ) {
                                val id = it.arguments?.getLong("id")!!
                                val type = it.arguments?.getString("type")
                                    ?.let { Transaction.Type.valueOf(it.uppercase()) }
                                    ?: Transaction.Type.EXPENSE
                                val count = it.arguments?.getLong("count") ?: 0L
                                ChangeCategoryInTransactionScreen(
                                    onNavigateBack = {
                                        navController.popBackStack()
                                    },
                                    categoryIdToBeDeleted = id,
                                    type = type,
                                    transactionCount = count
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}