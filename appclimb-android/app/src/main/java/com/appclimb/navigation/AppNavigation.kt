package com.appclimb.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.appclimb.ui.auth.LoginScreen
import com.appclimb.ui.auth.RegisterScreen
import com.appclimb.ui.calendar.CalendarScreen
import com.appclimb.ui.gym.GymListScreen
import com.appclimb.ui.record.RecordScreen

sealed class Screen(val route: String) {
    object Login    : Screen("login")
    object Register : Screen("register")
    object Main     : Screen("main")
}

sealed class BottomTab(val route: String, val label: String, val icon: ImageVector) {
    object Gyms     : BottomTab("gyms", "지점", Icons.Default.Place)
    object Calendar : BottomTab("calendar", "캘린더", Icons.Default.CalendarMonth)
    object Record   : BottomTab("record", "기록", Icons.Default.FitnessCenter)
}

val bottomTabs = listOf(BottomTab.Gyms, BottomTab.Calendar, BottomTab.Record)

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

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomTabs.forEach { tab ->
                    NavigationBarItem(
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) },
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
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomTab.Gyms.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomTab.Gyms.route)     { GymListScreen() }
            composable(BottomTab.Calendar.route) { CalendarScreen() }
            composable(BottomTab.Record.route)   { RecordScreen() }
        }
    }
}
