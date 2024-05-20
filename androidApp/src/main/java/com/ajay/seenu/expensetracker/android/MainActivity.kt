package com.ajay.seenu.expensetracker.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ajay.seenu.expensetracker.android.presentation.navigation.MainScreen
import com.ajay.seenu.expensetracker.android.presentation.screeens.AddTransactionScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "default",
                        modifier = Modifier.fillMaxSize(),
                        route = "MainScreenRoute"
                    ) {
                        composable("default") {
                            MainScreen (
                                onAddTransaction = {
                                    navController.navigate("add_transaction/-1L")
                                },
                                onCloneTransaction = {
                                    navController.navigate("add_transaction/${it}")
                                }
                            )
                        }
                        composable("add_transaction/{clone_id}",
                            arguments = listOf(
                                navArgument("clone_id") {
                                    type = NavType.LongType
                                }
                            )
                        ) {
                            var cloneId = it.arguments?.getLong("clone_id")
                            if(cloneId == -1L) cloneId = null
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