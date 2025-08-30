package com.ajay.seenu.expensetracker.android.presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ajay.seenu.expensetracker.android.presentation.screeens.OverviewScreen
import com.ajay.seenu.expensetracker.android.presentation.screeens.SettingsScreen
import com.ajay.seenu.expensetracker.android.presentation.screeens.SimpleAnalyticsScreen
import com.ajay.seenu.expensetracker.android.presentation.screeens.budget.BudgetScreen
import com.ajay.seenu.expensetracker.android.presentation.viewmodels.BudgetViewModel

@Composable
fun MainScreen(
    budgetViewModel: BudgetViewModel,   //shared ViewModel for budgets
    onAddTransaction: () -> Unit,
    onTransactionClicked: (Long) -> Unit,
    onCloneTransaction: (Long) -> Unit,
    onCategoryListScreen: () -> Unit,
    onCreateBudget: () -> Unit,
    onBudgetClick: (Long) -> Unit,
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    var currentRoute = navBackStackEntry?.destination?.route
    var isFABVisible by remember {
        mutableStateOf(true)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        bottomBar = {
            NavigationBar() {
                getBottomNavigationItems().forEachIndexed { index, navigationItem ->
                    val isSelected = currentRoute == navigationItem.route
                    NavigationBarItem(
                        selected = isSelected,
                        label = {
                            //Text(navigationItem.label)
                        },
                        icon = {
                            Icon(
                                modifier = Modifier.size(28.dp),
                                imageVector = if (isSelected) navigationItem.filledIcon else navigationItem.outlinedIcon,
                                contentDescription = navigationItem.label
                            )
                        },
                        onClick = {
                            currentRoute = navigationItem.route
                            navController.navigate(navigationItem.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            AnimatedVisibility(
                visible = isFABVisible,
                enter = scaleIn(animationSpec = tween(100)),
                exit = scaleOut(animationSpec = tween(100)),
            ) {
                FloatingActionButton(
                    onClick = onAddTransaction,
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = "Add transaction")
                }
            }
        }
    ) { paddingValues ->
        navController.addOnDestinationChangedListener { _, destination, _ ->
            isFABVisible = destination.route == Screen.Overview.route
        }
        NavHost(
            navController = navController,
            startDestination = Screen.Overview.route,
            modifier = Modifier.padding(paddingValues = paddingValues)
        ) {
            composable(Screen.Overview.route) {
                OverviewScreen(
                    onTransactionClicked = onTransactionClicked,
                    onCloneTransaction = onCloneTransaction,
                    onCategoryListScreen = onCategoryListScreen
                )
            }
            composable(Screen.Analytics.route) {
//                AnalyticsScreen(navController = navController)
                SimpleAnalyticsScreen()
            }
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
            composable(Screen.Budget.route) {
                BudgetScreen(
                    budgetViewModel = budgetViewModel,
                    onCreateBudget = onCreateBudget,
                    onBudgetClick = onBudgetClick,
                )
            }
        }
    }
}