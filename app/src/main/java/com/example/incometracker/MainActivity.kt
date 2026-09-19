package com.example.incometracker

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.incometracker.security.AppLockState
import com.example.incometracker.ui.add.AddIncomeScreen
import com.example.incometracker.ui.analytics.AnalyticsScreen
import com.example.incometracker.ui.calendar.CalendarScreen
import com.example.incometracker.ui.dashboard.DashboardScreen
import com.example.incometracker.ui.lock.PinSetupScreen
import com.example.incometracker.ui.lock.UnlockScreen
import com.example.incometracker.ui.settings.SettingsScreen
import com.example.incometracker.ui.theme.IncomeTheme
import java.time.LocalDate

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AppRoot() }
    }
}

sealed class Route(val value: String) {
    data object Dashboard : Route("dashboard")
    data object Calendar : Route("calendar")
    data object Analytics : Route("analytics")
    data object Settings : Route("settings")
    data object PinSetup : Route("pin_setup")
    data object AddIncome : Route("add_income?date={date}&entryId={entryId}")
}

@Composable
fun AppRoot() {
    IncomeTheme {
        val locked by AppLockState.locked.collectAsState()
        if (locked) UnlockScreen() else MainScaffoldNav()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScaffoldNav() {
    val nav = rememberNavController()
    val backStack by nav.currentBackStackEntryAsState()
    val current = backStack?.destination?.route

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = current == Route.Dashboard.value,
                    onClick = {
                        nav.navigate(Route.Dashboard.value) {
                            launchSingleTop = true
                            popUpTo(Route.Dashboard.value)
                        }
                    },
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = current == Route.Calendar.value,
                    onClick = {
                        nav.navigate(Route.Calendar.value) {
                            launchSingleTop = true
                            popUpTo(Route.Dashboard.value)
                        }
                    },
                    icon = { Icon(Icons.Filled.CalendarMonth, contentDescription = "Calendar") },
                    label = { Text("Calendar") }
                )
                NavigationBarItem(
                    selected = current == Route.Analytics.value,
                    onClick = {
                        nav.navigate(Route.Analytics.value) {
                            launchSingleTop = true
                            popUpTo(Route.Dashboard.value)
                        }
                    },
                    icon = { Icon(Icons.Filled.BarChart, contentDescription = "Analytics") },
                    label = { Text("Analytics") }
                )
                NavigationBarItem(
                    selected = current == Route.Settings.value,
                    onClick = {
                        nav.navigate(Route.Settings.value) {
                            launchSingleTop = true
                            popUpTo(Route.Dashboard.value)
                        }
                    },
                    icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = Route.Dashboard.value,
            modifier = Modifier.padding(padding)
        ) {
            composable(Route.Dashboard.value) {
                DashboardScreen(onAdd = { nav.navigate("add_income?date=-1&entryId=-1") })
            }
            composable(Route.Calendar.value) {
                CalendarScreen(
                    onAddForDate = { date ->
                        nav.navigate("add_income?date=${date.toEpochDay()}&entryId=-1")
                    },
                    onEditEntry = { id ->
                        nav.navigate("add_income?date=-1&entryId=$id")
                    }
                )
            }
            composable(Route.Analytics.value) { AnalyticsScreen() }
            composable(Route.Settings.value) {
                SettingsScreen(onOpenPinSetup = { nav.navigate(Route.PinSetup.value) })
            }
            composable(Route.PinSetup.value) {
                PinSetupScreen(onDone = { nav.popBackStack() })
            }
            composable(
                route = Route.AddIncome.value,
                arguments = listOf(
                    navArgument("date") { type = NavType.LongType; defaultValue = -1L },
                    navArgument("entryId") { type = NavType.LongType; defaultValue = -1L }
                )
            ) { backStack ->
                val epochDay = backStack.arguments?.getLong("date") ?: -1L
                val entryId = backStack.arguments?.getLong("entryId") ?: -1L
                val initialDate = if (epochDay == -1L) null else LocalDate.ofEpochDay(epochDay)
                val editId = if (entryId == -1L) null else entryId
                AddIncomeScreen(
                    onDone = { nav.popBackStack() },
                    initialDate = initialDate,
                    entryId = editId
                )
            }
        }
    }
}
