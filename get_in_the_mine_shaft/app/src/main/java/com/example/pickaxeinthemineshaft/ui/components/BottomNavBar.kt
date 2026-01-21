package com.example.pickaxeinthemineshaft.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.pickaxeinthemineshaft.navigation.Screen

@Composable
fun BottomNavBar(navController: NavController) {
    val navItems = listOf(
        Triple(Screen.Home.route, "Home", Icons.Default.Home),
        Triple(Screen.TaskList.route, "Tasks", Icons.Default.List),
        Triple(Screen.Calendar.route, "Calendar", Icons.Default.DateRange),
        Triple(Screen.Avatar.route, "Avatar", Icons.Default.Person),
        Triple(Screen.Statistics.route, "Stats", Icons.Default.Info)
    )
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    BottomNavigation {
        navItems.forEach { (route, label, icon) ->
            BottomNavigationItem(
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) },
                selected = currentRoute == route,
                onClick = {
                    if (currentRoute != route) {
                        navController.navigate(route) {
                            // Standard bottom-nav behavior:
                            // - pop up to the start destination to avoid building a huge back stack
                            // - save/restore state so each tab keeps its scroll position, etc.
                            // Note: Using findStartDestination() ensures Home button always navigates to actual start destination
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
} 