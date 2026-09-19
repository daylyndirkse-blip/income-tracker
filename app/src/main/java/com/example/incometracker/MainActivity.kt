package com.example.incometracker

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.incometracker.ui.profile.ProfileScreen
import com.example.incometracker.ui.settings.SettingsScreen
import com.example.incometracker.ui.theme.*
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
    data object Profile : Route("profile")
    data object PinSetup : Route("pin_setup")
    data object AddIncome : Route("add_income?date={date}&entryId={entryId}")
}

@Composable
fun AppRoot() {
    IncomeTheme {
        val locked by AppLockState.locked.collectAsState()
        AnimatedContent(
            targetState = locked,
            transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
            label = "lock_transition"
        ) { isLocked ->
            if (isLocked) UnlockScreen() else MainScaffoldNav()
        }
    }
}

private data class NavItem(val route: Route, val icon: ImageVector, val label: String)

@Composable
private fun MainScaffoldNav() {
    val nav = rememberNavController()
    val backStack by nav.currentBackStackEntryAsState()
    val current = backStack?.destination?.route

    val navItems = listOf(
        NavItem(Route.Dashboard, Icons.Filled.Home, "Home"),
        NavItem(Route.Calendar, Icons.Filled.CalendarMonth, "Calendar"),
        NavItem(Route.Analytics, Icons.Filled.BarChart, "Analytics"),
        NavItem(Route.Profile, Icons.Filled.Person, "Profile")
    )

    val showBottomBar = current in navItems.map { it.route.value }

    Scaffold(
        containerColor = Bg,
        bottomBar = {
            if (showBottomBar) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // Background bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .clip(RoundedCornerShape(35.dp))
                            .background(CardBg)
                            .align(Alignment.BottomCenter)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left 2 items
                        navItems.take(2).forEach { item ->
                            NavBarItem(
                                item = item,
                                selected = current == item.route.value,
                                onClick = {
                                    nav.navigate(item.route.value) {
                                        launchSingleTop = true
                                        popUpTo(Route.Dashboard.value) { saveState = true }
                                        restoreState = true
                                    }
                                }
                            )
                        }

                        // Center FAB
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .shadow(12.dp, CircleShape)
                                .clip(CircleShape)
                                .background(Purple),
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(onClick = { nav.navigate("add_income?date=-1&entryId=-1") }) {
                                Icon(
                                    Icons.Filled.Add,
                                    contentDescription = "Add",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        // Right 2 items
                        navItems.takeLast(2).forEach { item ->
                            NavBarItem(
                                item = item,
                                selected = current == item.route.value,
                                onClick = {
                                    nav.navigate(item.route.value) {
                                        launchSingleTop = true
                                        popUpTo(Route.Dashboard.value) { saveState = true }
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = Route.Dashboard.value,
            modifier = Modifier.padding(padding),
            enterTransition = { fadeIn(tween(250)) + slideInHorizontally(tween(250)) { it / 8 } },
            exitTransition = { fadeOut(tween(200)) },
            popEnterTransition = { fadeIn(tween(250)) },
            popExitTransition = { fadeOut(tween(200)) + slideOutHorizontally(tween(250)) { it / 8 } }
        ) {
            composable(Route.Dashboard.value) {
                DashboardScreen(onAdd = { nav.navigate("add_income?date=-1&entryId=-1") })
            }
            composable(Route.Calendar.value) {
                CalendarScreen(
                    onAddForDate = { date -> nav.navigate("add_income?date=${date.toEpochDay()}&entryId=-1") },
                    onEditEntry = { id -> nav.navigate("add_income?date=-1&entryId=$id") }
                )
            }
            composable(Route.Analytics.value) { AnalyticsScreen() }
            composable(Route.Profile.value) {
                ProfileScreen(
                    onOpenSettings = { nav.navigate(Route.Settings.value) },
                    onOpenPinSetup = { nav.navigate(Route.PinSetup.value) }
                )
            }
            composable(Route.Settings.value) {
                SettingsScreen(onOpenPinSetup = { nav.navigate(Route.PinSetup.value) })
            }
            composable(Route.PinSetup.value) { PinSetupScreen(onDone = { nav.popBackStack() }) }
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

@Composable
private fun NavBarItem(item: NavItem, selected: Boolean, onClick: () -> Unit) {
    val animatedColor by animateColorAsState(
        targetValue = if (selected) Purple else Color(0xFF666666),
        animationSpec = tween(200),
        label = "nav_color"
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        IconButton(onClick = onClick, modifier = Modifier.size(36.dp)) {
            Icon(item.icon, contentDescription = item.label, tint = animatedColor, modifier = Modifier.size(22.dp))
        }
        Text(item.label, color = animatedColor, fontSize = 10.sp, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal)
    }
}
