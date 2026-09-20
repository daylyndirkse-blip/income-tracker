package com.example.incometracker

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.incometracker.ui.analytics.AnalyticsScreen
import com.example.incometracker.ui.calendar.CalendarScreen
import com.example.incometracker.ui.dashboard.DashboardScreen
import com.example.incometracker.ui.insights.InsightsScreen
import com.example.incometracker.ui.profile.ProfileScreen
import com.example.incometracker.ui.settings.SettingsScreen

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Home)
    object Analytics : Screen("analytics", "Analytics", Icons.Default.ShowChart)
    object Calendar : Screen("calendar", "Calendar", Icons.Default.CalendarMonth)
    object Insights : Screen("insights", "Insights", Icons.Default.Lightbulb)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

@Composable
fun IncomeApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    val bottomNavItems = listOf(
        Screen.Dashboard,
        Screen.Analytics,
        Screen.Calendar,
        Screen.Insights,
        Screen.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { screen ->
                    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = selected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onAddClick = { /* TODO */ },
                    onSettingsClick = { navController.navigate(Screen.Settings.route) }
                )
            }
            
            composable(Screen.Analytics.route) {
                AnalyticsScreen()
            }
            
            composable(Screen.Calendar.route) {
                CalendarScreen()
            }
            
            composable(Screen.Insights.route) {
                InsightsScreen()
            }
            
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onSettingsClick = { navController.navigate(Screen.Settings.route) }
                )
            }
            
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
