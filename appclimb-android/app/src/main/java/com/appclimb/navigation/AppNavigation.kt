package com.appclimb.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.appclimb.ui.auth.LoginScreen
import com.appclimb.ui.auth.RegisterScreen
import com.appclimb.ui.calendar.CalendarScreen
import com.appclimb.ui.event.EventScreen
import com.appclimb.ui.gym.GymDetailScreen
import com.appclimb.ui.gym.GymListScreen
import com.appclimb.ui.profile.ProfileScreen
import com.appclimb.ui.record.RecordScreen
import com.appclimb.ui.stats.StatsScreen

sealed class Screen(val route: String) {
    object Login    : Screen("login")
    object Register : Screen("register")
    object Main     : Screen("main")
}

sealed class BottomTab(val route: String, val label: String, val icon: ImageVector) {
    object Gyms     : BottomTab("gyms",     "지점",   Icons.Default.Place)
    object Calendar : BottomTab("calendar", "캘린더", Icons.Default.CalendarMonth)
    object Events   : BottomTab("events",   "이벤트", Icons.Default.EmojiEvents)
    object Record   : BottomTab("record",   "기록",   Icons.Default.FitnessCenter)
    object Stats    : BottomTab("stats",    "통계",   Icons.Default.ShowChart)
    object Profile  : BottomTab("profile",  "프로필", Icons.Default.Person)
}

val bottomTabs = listOf(
    BottomTab.Gyms,
    BottomTab.Calendar,
    BottomTab.Events,
    BottomTab.Record,
    BottomTab.Stats,
    BottomTab.Profile
)

private const val GYM_DETAIL_ROUTE = "gym_detail/{gymId}"

@Composable
fun AppNavigation(isLoggedIn: Boolean) {
    val rootNav = rememberNavController()
    val startDest = if (isLoggedIn) Screen.Main.route else Screen.Login.route

    NavHost(navController = rootNav, startDestination = startDest) {

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    rootNav.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onGoToRegister = { rootNav.navigate(Screen.Register.route) }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    rootNav.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onBack = { rootNav.popBackStack() }
            )
        }

        composable(Screen.Main.route) {
            MainScreen(
                onLogout = {
                    rootNav.navigate(Screen.Login.route) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
fun MainScreen(onLogout: () -> Unit) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.route != GYM_DETAIL_ROUTE

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomTabs.forEach { tab ->
                        NavigationBarItem(
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label, style = MaterialTheme.typography.labelSmall) },
                            selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true,
                            onClick = {
                                navController.navigate(tab.route) {
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
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomTab.Gyms.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomTab.Gyms.route) {
                GymListScreen(onGymClick = { gymId -> navController.navigate("gym_detail/$gymId") })
            }
            composable(
                route = GYM_DETAIL_ROUTE,
                arguments = listOf(navArgument("gymId") { type = NavType.LongType })
            ) {
                GymDetailScreen(onBack = { navController.popBackStack() })
            }
            composable(BottomTab.Calendar.route) { CalendarScreen() }
            composable(BottomTab.Events.route)   { EventScreen() }
            composable(BottomTab.Record.route)   { RecordScreen() }
            composable(BottomTab.Stats.route)    { StatsScreen() }
            composable(BottomTab.Profile.route)  { ProfileScreen(onLogout = onLogout) }
        }
    }
}
